package learn.DeVitoStyles.domain.checkout;

import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import learn.DeVitoStyles.data.interfaces.CartItemRepository;
import learn.DeVitoStyles.data.interfaces.ProductRepository;
import learn.DeVitoStyles.data.interfaces.orders.OrderItemRepository;
import learn.DeVitoStyles.data.interfaces.orders.OrderRepository;
import learn.DeVitoStyles.data.interfaces.orders.PaymentRepository;
import learn.DeVitoStyles.models.Checkout.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, ProductRepository productRepository, CartItemRepository cartItemRepository, OrderItemRepository orderItemRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public void processStripeEvent(Event event) {

        /*
         * Every Stripe webhook has an event type.
         *
         * Examples:
         *
         * checkout.session.completed
         * payment_intent.succeeded
         * payment_intent.payment_failed
         */
        switch (event.getType()) {

            case "checkout.session.completed" ->
                    handleCheckoutCompleted(event);

            default ->
                    System.out.println(
                            "Unhandled Stripe event: "
                                    + event.getType()
                    );
        }

    }


    /*
     * Customer successfully completed Stripe Checkout.
     */
    private void handleCheckoutCompleted(Event event) {

        StripeObject stripeObject =
                event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

        if (!(stripeObject instanceof Session session)) {
            return;
        }

        Integer orderId = extractOrderId(session);

        if (orderId == null) {
            return;
        }

        Payment payment = paymentRepository.findByOrderId(orderId);

        if (payment == null) {
            return;
        }

        Order order = orderRepository.findById(orderId);

        if (order == null) {
            return;
        }

        markPaymentPaid(payment, session);
        makeOrderPaid(order);
        updateInventory(orderId);
        clearCart(order.getUserId());
    }

    private void clearCart(int userId) {
        cartItemRepository.clearCart(userId);
    }

    private void updateInventory(Integer orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : items) {
            productRepository.decreaseInventory(item.getProductId(), item.getQuantity());
        }
    }

    private void makeOrderPaid(Order order) {
        orderRepository.updateStatus(order.getOrderId(), OrderStatus.Paid);
    }

    private Integer extractOrderId(Session session) {

        String value = session.getMetadata().get("orderId");

        if (value == null) {
            return null;
        }

        return Integer.parseInt(value);
    }

    private void markPaymentPaid(Payment payment, Session session) {
        paymentRepository.updatePaymentIntent(payment.getPaymentId(), session.getPaymentIntent());
        paymentRepository.updateStatus(payment.getPaymentId(), PaymentStatus.Paid);
    }
}