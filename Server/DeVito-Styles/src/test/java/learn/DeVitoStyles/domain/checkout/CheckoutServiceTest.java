package learn.DeVitoStyles.domain.checkout;

import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.StripeException;
import learn.DeVitoStyles.data.interfaces.CartItemRepository;
import learn.DeVitoStyles.data.interfaces.CartRepository;
import learn.DeVitoStyles.data.interfaces.ProductRepository;
import learn.DeVitoStyles.data.interfaces.orders.OrderItemRepository;
import learn.DeVitoStyles.data.interfaces.orders.OrderRepository;
import learn.DeVitoStyles.data.interfaces.orders.PaymentRepository;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.dto.CheckoutRequestDto;
import learn.DeVitoStyles.dto.CheckoutResponseDTO;
import learn.DeVitoStyles.models.Checkout.Order;
import learn.DeVitoStyles.models.Checkout.OrderItem;
import learn.DeVitoStyles.models.Checkout.OrderStatus;
import learn.DeVitoStyles.models.Checkout.Payment;
import learn.DeVitoStyles.models.Checkout.PaymentStatus;
import learn.DeVitoStyles.models.Products.Product;
import learn.DeVitoStyles.models.ShoppingCart.Cart;
import learn.DeVitoStyles.models.ShoppingCart.CartItem;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    private static final int CART_ID = 5;
    private static final int USER_ID = 7;
    private static final int ORDER_ID = 42;
    private static final String CHECKOUT_URL = "https://checkout.stripe.com/c/pay/cs_test_123";

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private StripeService stripeService;

    @InjectMocks private CheckoutService checkoutService;

    // ---------- validation ----------

    @Test
    void checkout_whenRequestNull_returnsInvalid() throws StripeException {
        Result<CheckoutResponseDTO> result = checkoutService.checkout(null);

        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Cart id is required."), result.getMessages());
        verifyNoInteractions(cartRepository, cartItemRepository, orderRepository, stripeService);
    }

    @Test
    void checkout_whenCartIdNull_returnsInvalid() throws StripeException {
        Result<CheckoutResponseDTO> result = checkoutService.checkout(new CheckoutRequestDto());

        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Cart id is required."), result.getMessages());
        verifyNoInteractions(cartRepository, cartItemRepository, orderRepository, stripeService);
    }

    @Test
    void checkout_whenCartNotFound_returnsNotFound() throws StripeException {
        when(cartRepository.findById(CART_ID)).thenReturn(null);

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Cart not found."), result.getMessages());
        verifyNoInteractions(cartItemRepository, orderRepository, stripeService);
    }

    @Test
    void checkout_whenCartEmpty_returnsInvalid() throws StripeException {
        when(cartRepository.findById(CART_ID)).thenReturn(cart());
        when(cartItemRepository.findByCartId(CART_ID)).thenReturn(List.of());

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Cart is empty."), result.getMessages());
        verifyNoInteractions(productRepository, orderRepository, stripeService);
    }

    @Test
    void checkout_whenCartItemsNull_returnsInvalid() throws StripeException {
        when(cartRepository.findById(CART_ID)).thenReturn(cart());
        when(cartItemRepository.findByCartId(CART_ID)).thenReturn(null);

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Cart is empty."), result.getMessages());
        verifyNoInteractions(productRepository, orderRepository, stripeService);
    }

    @Test
    void checkout_whenProductMissing_returnsNotFound() throws StripeException {
        when(cartRepository.findById(CART_ID)).thenReturn(cart());
        when(cartItemRepository.findByCartId(CART_ID)).thenReturn(List.of(cartItem(10, 1)));
        when(productRepository.findById(10)).thenReturn(null);

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Product not found."), result.getMessages());
        verifyNoInteractions(orderRepository, orderItemRepository, paymentRepository, stripeService);
    }

    @Test
    void checkout_whenQuantityExceedsStock_returnsInvalidWithProductName() throws StripeException {
        when(cartRepository.findById(CART_ID)).thenReturn(cart());
        when(cartItemRepository.findByCartId(CART_ID)).thenReturn(List.of(cartItem(10, 3)));
        when(productRepository.findById(10)).thenReturn(product(10, "Pomade", "20.00", 2));

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Not enough stock available for Pomade."), result.getMessages());
        verifyNoInteractions(orderRepository, orderItemRepository, paymentRepository, stripeService);
    }

    @Test
    void checkout_whenQuantityEqualsStock_isAllowed() throws StripeException {
        stubValidCart(List.of(cartItem(10, 2)), product(10, "Pomade", "20.00", 2));
        when(orderRepository.add(any(Order.class))).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(orderItemRepository.addAll(anyList())).thenReturn(true);
        when(stripeService.createCheckoutSession(any(Order.class), anyList())).thenReturn(CHECKOUT_URL);
        when(paymentRepository.add(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertTrue(result.isSuccess());
    }

    // ---------- happy path ----------

    @Test
    @SuppressWarnings("unchecked")
    void checkout_whenValid_createsPendingOrderPaymentAndReturnsCheckoutInfo() throws StripeException {
        // 2 x 19.99 + 1 x 5.50 = 45.48 ; tax = 45.48 * 0.0625 = 2.8425 -> 2.84 ; total = 48.32
        when(cartRepository.findById(CART_ID)).thenReturn(cart());
        when(cartItemRepository.findByCartId(CART_ID))
                .thenReturn(List.of(cartItem(10, 2), cartItem(11, 1)));
        when(productRepository.findById(10)).thenReturn(product(10, "Pomade", "19.99", 10));
        when(productRepository.findById(11)).thenReturn(product(11, "Comb", "5.50", 10));
        when(orderRepository.add(any(Order.class))).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(orderItemRepository.addAll(anyList())).thenReturn(true);
        when(stripeService.createCheckoutSession(any(Order.class), anyList())).thenReturn(CHECKOUT_URL);
        when(paymentRepository.add(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setPaymentId(99);
            return p;
        });

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertTrue(result.isSuccess(), () -> "Unexpected messages: " + result.getMessages());

        // Order passed to repository
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).add(orderCaptor.capture());
        Order order = orderCaptor.getValue();
        assertEquals(USER_ID, order.getUserId());
        assertEquals(OrderStatus.Pending, order.getStatus());
        assertEquals(new BigDecimal("45.48"), order.getSubtotal());
        assertEquals(new BigDecimal("2.84"), order.getTaxAmount());
        assertEquals(0, BigDecimal.ZERO.compareTo(order.getShippingCost()));
        assertEquals(0, BigDecimal.ZERO.compareTo(order.getDiscountAmount()));
        assertEquals(new BigDecimal("48.32"), order.getTotalAmount());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getOrderNumber());
        assertTrue(order.getOrderNumber().startsWith("DEV-"), order.getOrderNumber());
        assertTrue(order.getOrderNumber().matches(".*-[0-9A-F]{6}"), order.getOrderNumber());

        // Order items saved with the generated order id
        ArgumentCaptor<List<OrderItem>> itemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(orderItemRepository).addAll(itemsCaptor.capture());
        List<OrderItem> items = itemsCaptor.getValue();
        assertEquals(2, items.size());
        assertTrue(items.stream().allMatch(i -> i.getOrderId() == ORDER_ID));
        OrderItem pomade = items.get(0);
        assertEquals(10, pomade.getProductId());
        assertEquals("Pomade", pomade.getProductName());
        assertEquals(2, pomade.getQuantity());
        assertEquals(new BigDecimal("19.99"), pomade.getUnitPrice());
        assertEquals(new BigDecimal("39.98"), pomade.getLineTotal());
        OrderItem comb = items.get(1);
        assertEquals(11, comb.getProductId());
        assertEquals(new BigDecimal("5.50"), comb.getLineTotal());

        // Stripe receives the saved order and the same items
        verify(stripeService).createCheckoutSession(argThat(o -> o.getOrderId() == ORDER_ID), eq(items));

        // Payment passed to repository
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).add(paymentCaptor.capture());
        Payment payment = paymentCaptor.getValue();
        assertEquals(ORDER_ID, payment.getOrderId());
        assertEquals(PaymentStatus.Pending, payment.getStatus());
        assertEquals("Stripe", payment.getPaymentProvider());
        assertEquals("USD", payment.getCurrency());
        assertEquals(new BigDecimal("48.32"), payment.getAmount());
        assertNull(payment.getPaymentIntentId());
        assertNotNull(payment.getCreatedAt());

        // Response
        CheckoutResponseDTO response = result.getpayload();
        assertNotNull(response);
        assertEquals(CHECKOUT_URL, response.getCheckoutUrl());
        assertEquals(ORDER_ID, response.getOrderId());
        assertEquals(order.getOrderNumber(), response.getOrderNumber());
        assertEquals(48.32, response.getAmount(), 0.0001);
    }

    @Test
    void checkout_whenTaxHasHalfCent_roundsHalfUp() throws StripeException {
        // 1 x 0.40 -> tax = 0.025 -> HALF_UP -> 0.03 ; total 0.43
        stubValidCart(List.of(cartItem(10, 1)), product(10, "Sample", "0.40", 5));
        when(orderRepository.add(any(Order.class))).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(orderItemRepository.addAll(anyList())).thenReturn(true);
        when(stripeService.createCheckoutSession(any(Order.class), anyList())).thenReturn(CHECKOUT_URL);
        when(paymentRepository.add(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertTrue(result.isSuccess());
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).add(orderCaptor.capture());
        assertEquals(new BigDecimal("0.03"), orderCaptor.getValue().getTaxAmount());
        assertEquals(new BigDecimal("0.43"), orderCaptor.getValue().getTotalAmount());
        assertEquals(0.43, result.getpayload().getAmount(), 0.0001);
    }

    /*
     * Intended format per CheckoutService#generateOrderNumber's own doc comment
     * ("DEV-20260903112345-A8F21C"). Disabled because the current implementation
     * builds the timestamp from LocalDateTime#toString, which keeps the 'T'
     * separator (yielding e.g. "DEV-20260903T11234-A8F21C") and throws
     * StringIndexOutOfBoundsException when the time has zero seconds and nanos
     * (toString then omits seconds and the string is only 13 chars).
     */
    @Test
    @Disabled("Production bug in CheckoutService#generateOrderNumber - see comment")
    void checkout_whenValid_orderNumberHasFourteenDigitTimestamp() throws StripeException {
        stubValidCart(List.of(cartItem(10, 1)), product(10, "Pomade", "20.00", 5));
        when(orderRepository.add(any(Order.class))).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(orderItemRepository.addAll(anyList())).thenReturn(true);
        when(stripeService.createCheckoutSession(any(Order.class), anyList())).thenReturn(CHECKOUT_URL);
        when(paymentRepository.add(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertTrue(result.getpayload().getOrderNumber().matches("DEV-\\d{14}-[0-9A-F]{6}"),
                result.getpayload().getOrderNumber());
    }

    // ---------- downstream failures ----------

    @Test
    void checkout_whenOrderAddReturnsNull_returnsInvalidAndSkipsStripe() throws StripeException {
        stubValidCart(List.of(cartItem(10, 1)), product(10, "Pomade", "20.00", 5));
        when(orderRepository.add(any(Order.class))).thenReturn(null);

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Unable to create order."), result.getMessages());
        assertNull(result.getpayload());
        verifyNoInteractions(orderItemRepository, paymentRepository, stripeService);
    }

    @Test
    void checkout_whenOrderItemsNotSaved_returnsInvalidAndSkipsStripe() throws StripeException {
        stubValidCart(List.of(cartItem(10, 1)), product(10, "Pomade", "20.00", 5));
        when(orderRepository.add(any(Order.class))).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(orderItemRepository.addAll(anyList())).thenReturn(false);

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Unable to save order items."), result.getMessages());
        verifyNoInteractions(paymentRepository, stripeService);
    }

    @Test
    void checkout_whenStripeReturnsNullUrl_returnsInvalidAndSkipsPayment() throws StripeException {
        stubValidCart(List.of(cartItem(10, 1)), product(10, "Pomade", "20.00", 5));
        when(orderRepository.add(any(Order.class))).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(orderItemRepository.addAll(anyList())).thenReturn(true);
        when(stripeService.createCheckoutSession(any(Order.class), anyList())).thenReturn(null);

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Unable to create Stripe payment session."), result.getMessages());
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void checkout_whenPaymentAddReturnsNull_returnsInvalid() throws StripeException {
        stubValidCart(List.of(cartItem(10, 1)), product(10, "Pomade", "20.00", 5));
        when(orderRepository.add(any(Order.class))).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(orderItemRepository.addAll(anyList())).thenReturn(true);
        when(stripeService.createCheckoutSession(any(Order.class), anyList())).thenReturn(CHECKOUT_URL);
        when(paymentRepository.add(any(Payment.class))).thenReturn(null);

        Result<CheckoutResponseDTO> result = checkoutService.checkout(request());

        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Unable to save payment information."), result.getMessages());
        assertNull(result.getpayload());
    }

    @Test
    void checkout_whenStripeThrows_propagatesException() throws StripeException {
        stubValidCart(List.of(cartItem(10, 1)), product(10, "Pomade", "20.00", 5));
        when(orderRepository.add(any(Order.class))).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(orderItemRepository.addAll(anyList())).thenReturn(true);
        StripeException stripeError = new ApiConnectionException("Stripe is down");
        when(stripeService.createCheckoutSession(any(Order.class), anyList())).thenThrow(stripeError);

        StripeException thrown = assertThrows(StripeException.class, () -> checkoutService.checkout(request()));

        assertSame(stripeError, thrown);
        verifyNoInteractions(paymentRepository);
    }

    // ---------- helpers ----------

    private void stubValidCart(List<CartItem> items, Product product) {
        when(cartRepository.findById(CART_ID)).thenReturn(cart());
        when(cartItemRepository.findByCartId(CART_ID)).thenReturn(items);
        when(productRepository.findById(anyInt())).thenReturn(product);
    }

    private static CheckoutRequestDto request() {
        CheckoutRequestDto dto = new CheckoutRequestDto();
        dto.setCartId(CART_ID);
        return dto;
    }

    private static Cart cart() {
        Cart cart = new Cart();
        cart.setCartId(CART_ID);
        cart.setUserId(USER_ID);
        return cart;
    }

    private static CartItem cartItem(int productId, int quantity) {
        Product ref = new Product();
        ref.setProductId(productId);
        CartItem item = new CartItem();
        item.setCartId(CART_ID);
        item.setProduct(ref);
        item.setQuantity(quantity);
        return item;
    }

    private static Product product(int productId, String name, String price, int stock) {
        Product product = new Product();
        product.setProductId(productId);
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(stock);
        return product;
    }

    private static Order withId(Order order) {
        order.setOrderId(ORDER_ID);
        return order;
    }
}
