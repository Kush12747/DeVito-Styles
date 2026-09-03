package learn.DeVitoStyles.domain.checkout;

import learn.DeVitoStyles.data.interfaces.CartRepository;
import learn.DeVitoStyles.data.interfaces.ProductRepository;
import learn.DeVitoStyles.data.interfaces.orders.OrderItemRepository;
import learn.DeVitoStyles.data.interfaces.orders.OrderRepository;
import learn.DeVitoStyles.data.interfaces.orders.PaymentRepository;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.dto.CheckoutRequestDto;
import learn.DeVitoStyles.dto.CheckoutResponseDTO;
import learn.DeVitoStyles.models.Checkout.*;
import learn.DeVitoStyles.models.Products.Product;
import learn.DeVitoStyles.models.ShoppingCart.Cart;
import learn.DeVitoStyles.models.ShoppingCart.CartItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/*
 * CheckoutService is responsible for coordinating the checkout process.
 *
 * The overall flow is:
 *
 * Cart
 *   ↓
 * Validate cart
 *   ↓
 * Validate products / build OrderItems
 *   ↓
 * Calculate totals
 *   ↓
 * Create Order
 *   ↓
 * Save OrderItems
 *   ↓
 * Create Stripe Checkout Session
 *   ↓
 * Create Payment record
 *   ↓
 * Return checkout information to the frontend
 *
 * This class coordinates the process while repositories handle database
 * operations and StripeService handles communication with Stripe.
 */
@Service
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final StripeService stripeService;


    /*
     * Constructor injection.
     *
     * Spring creates these dependencies and passes them into this service.
     * This keeps CheckoutService from having to create its own repositories
     * or StripeService objects.
     */
    public CheckoutService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            ProductRepository productRepository,
            CartRepository cartRepository,
            StripeService stripeService
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.stripeService = stripeService;
    }


    /*
     * Main checkout workflow.
     *
     * This method intentionally reads like a checklist of the checkout
     * process instead of containing all the implementation details.
     *
     * IMPORTANT:
     * This method starts checkout and creates a PENDING order/payment.
     * It does not mean the customer has successfully paid yet.
     *
     * The actual payment confirmation should be handled separately through
     * Stripe's payment confirmation/webhook flow.
     */
    @Transactional
    public Result<CheckoutResponseDTO> checkout(CheckoutRequestDto requestDto) {

        Result<CheckoutResponseDTO> result = new Result<>();

        /*
         * STEP 1:
         * Find and validate the customer's cart.
         */
        Cart cart = getAndValidateCart(requestDto.getCartId(), result);

        if (cart == null) {
            return result;
        }


        /*
         * STEP 2:
         * Validate the products in the cart and convert each CartItem
         * into an OrderItem.
         *
         * OrderItems represent the products that will become part
         * of the permanent order record.
         */
        List<OrderItem> orderItems = buildOrderItems(cart, result);

        if (orderItems == null) {
            return result;
        }


        /*
         * STEP 3:
         * Calculate subtotal, tax, shipping, discount and final total.
         */
        CheckoutTotals totals = calculateTotals(orderItems);


        /*
         * STEP 4:
         * Create the Order in the database.
         *
         * The order starts as PENDING because payment has not been
         * confirmed yet.
         */
        Order order = createOrder(
                cart,
                totals
        );

        if (order == null) {
            result.addErrorMessage("Unable to create order.", ResultType.INVALID);
            return result;
        }


        /*
         * STEP 5:
         * Associate every OrderItem with the newly-created Order.
         *
         * We could not do this earlier because we did not know the
         * Order ID until the Order was saved.
         */
        boolean itemsSaved = saveOrderItems(order, orderItems);

        if (!itemsSaved) {
            result.addErrorMessage("Unable to save order items.", ResultType.INVALID);
            return result;
        }


        /*
         * STEP 6:
         * Create the Stripe Checkout Session.
         *
         * StripeService handles the actual Stripe API communication.
         * CheckoutService only coordinates when Stripe should be called.
         */
        String clientSecret = stripeService.createCheckoutSession(orderItems, order.getOrderId());

        if (clientSecret == null) {
            result.addErrorMessage("Unable to create Stripe payment session.", ResultType.INVALID);
            return result;
        }


        /*
         * STEP 7:
         * Create our application's Payment record.
         *
         * This record starts as PENDING because the customer has not
         * necessarily completed payment yet.
         */
        Payment payment = createPayment(order, totals
        );

        if (payment == null) {
            result.addErrorMessage("Unable to save payment information.", ResultType.INVALID);
            return result;
        }


        /*
         * STEP 8:
         * Build the response that will eventually be returned to React.
         */
        CheckoutResponseDTO response = buildCheckoutResponse(clientSecret, order, totals);

        result.setpayload(response);

        return result;
    }


    /*
     * Finds the customer's cart and performs the basic validation required
     * before checkout can continue.
     *
     * Returns:
     *   Cart -> if the cart exists and contains items
     *   null -> if validation fails
     *
     * The Result object is used to store the appropriate error message.
     */
    private Cart getAndValidateCart(int cartId, Result<?> result) {

        /*
         * Ask the CartRepository to find the cart in the database.
         */
        Cart cart = cartRepository.findById(cartId);


        /*
         * A null cart means no cart was found with this ID.
         */
        if (cart == null) {
            result.addErrorMessage("Cart not found.", ResultType.NOT_FOUND);
            return null;
        }


        /*
         * A cart must contain at least one item before checkout
         * can continue.
         */
        List<CartItem> cartItems = cart.getItems();

        if (cartItems == null || cartItems.isEmpty()) {
            result.addErrorMessage("Cart is empty.", ResultType.INVALID);
            return null;
        }

        return cart;
    }


    /*
     * Converts the CartItems into OrderItems.
     *
     * This method also validates that every product in the cart still
     * exists in the Product table.
     *
     * Example:
     *
     * CartItem:
     *   Product = Pomade
     *   Quantity = 2
     *
     * becomes:
     *
     * OrderItem:
     *   Product ID = 10
     *   Product Name = Pomade
     *   Quantity = 2
     *   Unit Price = $20
     *   Line Total = $40
     */
    private List<OrderItem> buildOrderItems(Cart cart, Result<?> result) {

        List<OrderItem> orderItems = new ArrayList<>();

        /*
         * Process every item currently in the customer's cart.
         */
        for (CartItem cartItem : cart.getItems()) {

            /*
             * Retrieve the current product from the database.
             *
             * We use the Product table as the source of truth for
             * the current product information and price.
             */
            Product product = productRepository.findById(cartItem.getProduct().getProductId());


            /*
             * The product may have been deleted after it was added
             * to the customer's cart.
             */
            if (product == null) {

                result.addErrorMessage("Product not found.", ResultType.NOT_FOUND);
                return null;
            }


            /*
             * Calculate the total price for this particular product.
             *
             * lineTotal = unit price × quantity
             *
             * Example:
             *
             * $25 × 2 = $50
             */
            BigDecimal lineTotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );


            /*
             * Create a permanent snapshot of the product being purchased.
             *
             * We store the name and price on OrderItem because product
             * information may change after the order has been placed.
             */
            OrderItem orderItem = new OrderItem();

            orderItem.setProductId(product.getProductId());

            orderItem.setProductName(product.getName());

            orderItem.setQuantity(cartItem.getQuantity());

            orderItem.setUnitPrice(product.getPrice());

            orderItem.setLineTotal(lineTotal);

            /*
             * The OrderItem does not have an order ID yet.
             *
             * That will be assigned after the Order is created.
             */
            orderItems.add(orderItem);
        }

        return orderItems;
    }


    /*
     * Calculates all monetary values for the checkout.
     *
     * Currently:
     *
     * subtotal = sum of all OrderItem line totals
     * tax      = 6.25%
     * shipping = $0
     * discount = $0
     * total    = subtotal + tax + shipping - discount
     *
     * NOTE:
     * This is a good candidate to eventually move into a separate
     * PricingService as your pricing rules become more complicated.
     */
    private CheckoutTotals calculateTotals(List<OrderItem> orderItems) {

        BigDecimal subtotal = BigDecimal.ZERO;

        /*
         * Add the price of every OrderItem to the subtotal.
         */
        for (OrderItem item : orderItems) {

            subtotal = subtotal.add(item.getLineTotal());
        }


        /*
         * Current tax rate:
         * 6.25%
         */
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.0625"));


        /*
         * Shipping is currently free.
         *
         * This can later be replaced with actual shipping calculations.
         */
        BigDecimal shipping = BigDecimal.ZERO;

        /*
         * Discounts/coupons are not currently implemented.
         */
        BigDecimal discount = BigDecimal.ZERO;

        /*
         * Calculate the final amount.
         *
         * total =
         * subtotal + tax + shipping - discount
         */
        BigDecimal total = subtotal.add(tax).add(shipping).subtract(discount);


        return new CheckoutTotals(subtotal, tax, shipping, discount, total);
    }


    /*
     * Creates and saves the Order.
     *
     * The Order begins in PENDING status because the customer has
     * not completed payment yet.
     */
    private Order createOrder(Cart cart, CheckoutTotals totals) {

        Order order = new Order();

        /*
         * Generate the customer-facing order number.
         */
        order.setOrderNumber(generateOrderNumber());


        /*
         * Associate the order with the user who owns the cart.
         */
        order.setUserId(cart.getUserId());

        /*
         * The order is not paid yet.
         */
        order.setStatus(OrderStatus.Pending);


        /*
         * Save all the calculated financial information on the Order.
         */
        order.setSubtotal(totals.getSubtotal());

        order.setTaxAmount(totals.getTax());

        order.setShippingCost(totals.getShipping());

        order.setDiscountAmount(totals.getDiscount());

        order.setTotalAmount(totals.getTotal());

        /*
         * Record when the order was created.
         */
        order.setCreatedAt(LocalDateTime.now());


        /*
         * Save the order to the database.
         *
         * The returned Order should contain the generated Order ID.
         */
        return orderRepository.add(order);
    }


    /*
     * Saves all OrderItems and associates them with the newly-created Order.
     *
     * The Order must already exist because its database ID is needed
     * as the foreign key on each OrderItem.
     */
    private boolean saveOrderItems(Order order, List<OrderItem> orderItems) {

        /*
         * Assign the Order ID to every OrderItem.
         */
        for (OrderItem item : orderItems) {

            item.setOrderId(order.getOrderId());
        }

        /*
         * Save all OrderItems in the database.
         */
        return orderItemRepository.addAll(orderItems);
    }


    /*
     * Creates our application's Payment record.
     *
     * This does NOT mean Stripe has confirmed payment.
     *
     * The record begins as PENDING and should be updated later when
     * Stripe confirms the payment.
     */
    private Payment createPayment(Order order, CheckoutTotals totals) {

        Payment payment = new Payment();

        /*
         * Connect the Payment to our Order.
         */
        payment.setOrderId(order.getOrderId());


        /*
         * Stripe is the payment provider.
         */
        payment.setPaymentProvider("Stripe");


        /*
         * We do not have the final PaymentIntent ID at this stage
         * of the checkout process.
         *
         * This should be populated later when appropriate.
         */
        payment.setPaymentIntentId(null);


        /*
         * Payment starts as PENDING.
         */
        payment.setStatus(PaymentStatus.Pending);

        /*
         * Store the amount that is expected to be paid.
         */
        payment.setAmount(totals.getTotal());

        /*
         * Store the currency used for the transaction.
         */
        payment.setCurrency("USD");


        /*
         * Record when our payment record was created.
         */
        payment.setCreatedAt(LocalDateTime.now());

        /*
         * Save the payment record.
         */
        return paymentRepository.add(payment);
    }


    /*
     * Builds the object that is returned to the frontend after
     * checkout setup succeeds.
     *
     * The frontend needs the clientSecret to continue the Stripe
     * payment process.
     */
    private CheckoutResponseDTO buildCheckoutResponse(String clientSecret, Order order, CheckoutTotals totals) {

        return new CheckoutResponseDTO(
                clientSecret,
                order.getOrderId(),
                order.getOrderNumber(),
                totals.getTotal().doubleValue()
        );
    }


    /*
     * Generates a human-readable order number.
     *
     * Example:
     *
     * DEV-20260903112345-A8F21C
     *
     * DEV
     *   -> identifies the application/business
     *
     * timestamp
     *   -> gives the order number a useful date/time component
     *
     * UUID section
     *   -> provides additional uniqueness
     */
    private String generateOrderNumber() {

        return "DEV-"
                + LocalDateTime.now()
                .toString()
                .replace("-", "")
                .replace(":", "")
                .substring(0, 14)
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }
}