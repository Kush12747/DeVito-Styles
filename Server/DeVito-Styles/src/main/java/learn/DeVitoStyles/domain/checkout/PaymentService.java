package learn.DeVitoStyles.domain.checkout;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import learn.DeVitoStyles.data.interfaces.CartItemRepository;
import learn.DeVitoStyles.data.interfaces.CartRepository;
import learn.DeVitoStyles.data.interfaces.ProductRepository;
import learn.DeVitoStyles.data.interfaces.orders.OrderItemRepository;
import learn.DeVitoStyles.data.interfaces.orders.OrderRepository;
import learn.DeVitoStyles.data.interfaces.orders.PaymentRepository;
import learn.DeVitoStyles.models.Checkout.*;
import learn.DeVitoStyles.models.ShoppingCart.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            OrderItemRepository orderItemRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public void processStripeEvent(Event event) {
        switch (event.getType()) {
            case "checkout.session.completed",
                 "checkout.session.async_payment_succeeded" ->
                    handleCheckoutCompleted(event);

            case "checkout.session.async_payment_failed" ->
                    handleCheckoutFailed(event);

            case "payment_intent.succeeded" ->
                    handlePaymentIntentSucceeded(event);

            case "payment_intent.payment_failed" ->
                    handlePaymentIntentFailed(event);

            case "charge.refunded" ->
                    handleChargeRefunded(event);

            case "refund.updated" ->
                    handleRefundUpdated(event);

            case "refund.failed" ->
                    log.warn("Stripe refund failed: {}", event.getId());

            default ->
                    log.debug("Unhandled Stripe event: {}", event.getType());
        }
    }

    /**
     * Creates a full refund. Stripe will send refund.updated / charge.refunded
     * afterward; the immediate success case is handled as well.
     */
    public Refund refundOrder(int orderId) throws StripeException {
        Payment payment = paymentRepository.findByOrderId(orderId);

        if (payment == null) {
            throw new IllegalArgumentException("Payment not found for order " + orderId);
        }

        if (payment.getStatus() != PaymentStatus.Succeeded) {
            throw new IllegalStateException("Only paid orders can be refunded.");
        }

        if (payment.getPaymentIntentId() == null || payment.getPaymentIntentId().isBlank()) {
            throw new IllegalStateException("Payment intent is missing for order " + orderId);
        }

        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(payment.getPaymentIntentId())
                .putMetadata("orderId", String.valueOf(orderId))
                .build();

        Refund refund = Refund.create(params);

        if ("succeeded".equals(refund.getStatus())) {
            markOrderRefunded(payment);
        }

        return refund;
    }

    private void handleCheckoutCompleted(Event event) {
        StripeObject stripeObject = getStripeObject(event);

        if (!(stripeObject instanceof Session session)) {
            return;
        }

        // Delayed payment methods emit checkout.session.completed before payment.
        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            return;
        }

        Integer orderId = extractOrderId(session.getMetadata());

        if (orderId != null) {
            completePayment(orderId, session.getPaymentIntent());
        }
    }

    private void handleCheckoutFailed(Event event) {
        StripeObject stripeObject = getStripeObject(event);

        if (stripeObject instanceof Session session) {
            Integer orderId = extractOrderId(session.getMetadata());

            if (orderId != null) {
                markPaymentFailed(orderId);
            }
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        StripeObject stripeObject = getStripeObject(event);

        if (stripeObject instanceof PaymentIntent paymentIntent) {
            Integer orderId = extractOrderId(paymentIntent.getMetadata());

            if (orderId != null) {
                completePayment(orderId, paymentIntent.getId());
            }
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        StripeObject stripeObject = getStripeObject(event);

        if (stripeObject instanceof PaymentIntent paymentIntent) {
            Integer orderId = extractOrderId(paymentIntent.getMetadata());

            if (orderId != null) {
                markPaymentFailed(orderId);
            }
        }
    }

    private void handleChargeRefunded(Event event) {
        StripeObject stripeObject = getStripeObject(event);

        if (!(stripeObject instanceof Charge charge)) {
            return;
        }

        // Ignore partial refunds; this service's refundOrder method is full-refund only.
        if (!Boolean.TRUE.equals(charge.getRefunded())) {
            return;
        }

        Payment payment = paymentRepository.findByPaymentIntent(charge.getPaymentIntent());

        if (payment != null) {
            markOrderRefunded(payment);
        }
    }

    private void handleRefundUpdated(Event event) {
        StripeObject stripeObject = getStripeObject(event);

        if (!(stripeObject instanceof Refund refund)
                || !"succeeded".equals(refund.getStatus())) {
            return;
        }

        Integer orderId = extractOrderId(refund.getMetadata());

        if (orderId == null) {
            return;
        }

        Payment payment = paymentRepository.findByOrderId(orderId);

        if (payment != null) {
            markOrderRefunded(payment);
        }
    }

    private void completePayment(int orderId, String paymentIntentId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        Order order = orderRepository.findById(orderId);

        if (payment == null || order == null) {
            log.warn("Could not complete payment for order {}", orderId);
            return;
        }

        // Stripe retries webhooks; never decrease inventory twice.
        if (payment.getStatus() == PaymentStatus.Succeeded
                || payment.getStatus() == PaymentStatus.Refunded) {
            return;
        }

        if (paymentIntentId != null && !paymentIntentId.isBlank()) {
            paymentRepository.updatePaymentIntent(payment.getPaymentId(), paymentIntentId);
        }

        paymentRepository.updateStatus(payment.getPaymentId(), PaymentStatus.Succeeded);
        orderRepository.updateStatus(orderId, OrderStatus.Paid);

        decreaseInventory(orderId);
        clearUserCart(order.getUserId());
    }

    private void clearUserCart(int userId) {
        Cart cart = cartRepository.findByUserId(userId);

        if (cart != null) {
            cartItemRepository.clearCart(cart.getCartId());
        }
    }

    private void markPaymentFailed(int orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);

        if (payment == null
                || payment.getStatus() == PaymentStatus.Succeeded
                || payment.getStatus() == PaymentStatus.Refunded) {
            return;
        }

        paymentRepository.updateStatus(payment.getPaymentId(), PaymentStatus.Failed);
        orderRepository.updateStatus(orderId, OrderStatus.PaymentFailed);
    }

    private void markOrderRefunded(Payment payment) {
        if (payment.getStatus() == PaymentStatus.Refunded) {
            return;
        }

        paymentRepository.updateStatus(payment.getPaymentId(), PaymentStatus.Refunded);
        orderRepository.updateStatus(payment.getOrderId(), OrderStatus.Refunded);

        // Remove this call if inventory should only return after a physical return is received.
        increaseInventory(payment.getOrderId());
    }

    private void decreaseInventory(int orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : items) {
            boolean decreased = productRepository.decreaseInventory(item.getProductId(), item.getQuantity());

            // The customer has already paid, so don't fail the webhook; flag it for follow-up.
            if (!decreased) {
                log.warn("Insufficient stock for product {} on paid order {}", item.getProductId(), orderId);
            }
        }
    }

    private void increaseInventory(int orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : items) {
            productRepository.increaseInventory(item.getProductId(), item.getQuantity());
        }
    }

    private StripeObject getStripeObject(Event event) {
        return event.getDataObjectDeserializer().getObject().orElse(null);
    }

    private Integer extractOrderId(Map<String, String> metadata) {
        if (metadata == null || metadata.get("orderId") == null) {
            return null;
        }

        try {
            return Integer.parseInt(metadata.get("orderId"));
        } catch (NumberFormatException ex) {
            log.warn("Invalid Stripe orderId metadata: {}", metadata.get("orderId"));
            return null;
        }
    }
}