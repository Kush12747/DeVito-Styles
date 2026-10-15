package learn.DeVitoStyles.domain.checkout;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private OrderItemRepository orderItemRepository;

    @InjectMocks private PaymentService paymentService;

    @Test
    void processStripeEvent_whenCheckoutIsPaid_completesOrder() {
        int orderId = 12;
        int paymentId = 44;
        int userId = 7;

        Session session = mock(Session.class);
        Payment payment = mock(Payment.class);
        Order order = mock(Order.class);
        OrderItem item = mock(OrderItem.class);

        when(session.getPaymentStatus()).thenReturn("paid");
        when(session.getPaymentIntent()).thenReturn("pi_123");
        when(session.getMetadata()).thenReturn(Map.of("orderId", "12"));

        when(paymentRepository.findByOrderId(orderId)).thenReturn(payment);
        when(orderRepository.findById(orderId)).thenReturn(order);
        when(payment.getPaymentId()).thenReturn(paymentId);
        when(payment.getStatus()).thenReturn(null);
        when(order.getUserId()).thenReturn(userId);

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(item));
        when(item.getProductId()).thenReturn(9);
        when(item.getQuantity()).thenReturn(2);
        when(productRepository.decreaseInventory(9, 2)).thenReturn(true);

        Cart cart = new Cart();
        cart.setCartId(3);
        when(cartRepository.findByUserId(userId)).thenReturn(cart);

        paymentService.processStripeEvent(
                stripeEvent("checkout.session.completed", session)
        );

        verify(paymentRepository).updatePaymentIntent(paymentId, "pi_123");
        verify(paymentRepository).updateStatus(paymentId, PaymentStatus.Succeeded);
        verify(orderRepository).updateStatus(orderId, OrderStatus.Paid);
        verify(productRepository).decreaseInventory(9, 2);
        verify(cartItemRepository).clearCart(3);
    }

    @Test
    void processStripeEvent_whenPaymentAlreadyPaid_doesNotProcessTwice() {
        Session session = mock(Session.class);
        Payment payment = mock(Payment.class);
        Order order = mock(Order.class);

        when(session.getPaymentStatus()).thenReturn("paid");
        when(session.getPaymentIntent()).thenReturn("pi_123");
        when(session.getMetadata()).thenReturn(Map.of("orderId", "12"));

        when(paymentRepository.findByOrderId(12)).thenReturn(payment);
        when(orderRepository.findById(12)).thenReturn(order);
        when(payment.getStatus()).thenReturn(PaymentStatus.Succeeded);

        paymentService.processStripeEvent(
                stripeEvent("checkout.session.completed", session)
        );

        verify(paymentRepository, never()).updatePaymentIntent(anyInt(), anyString());
        verify(paymentRepository, never()).updateStatus(anyInt(), any());
        verify(orderRepository, never()).updateStatus(anyInt(), any());
        verifyNoInteractions(productRepository, cartRepository, cartItemRepository, orderItemRepository);
    }

    @Test
    void processStripeEvent_whenCheckoutFails_marksPaymentAndOrderFailed() {
        Session session = mock(Session.class);
        Payment payment = mock(Payment.class);

        when(session.getMetadata()).thenReturn(Map.of("orderId", "12"));
        when(paymentRepository.findByOrderId(12)).thenReturn(payment);
        when(payment.getPaymentId()).thenReturn(44);
        when(payment.getStatus()).thenReturn(null);

        paymentService.processStripeEvent(
                stripeEvent("checkout.session.async_payment_failed", session)
        );

        verify(paymentRepository).updateStatus(44, PaymentStatus.Failed);
        verify(orderRepository).updateStatus(12, OrderStatus.PaymentFailed);
        verifyNoInteractions(productRepository, cartRepository, cartItemRepository, orderItemRepository);
    }

    @Test
    void processStripeEvent_whenFullRefundOccurs_marksOrderRefundedAndRestocks() {
        Charge charge = mock(Charge.class);
        Payment payment = mock(Payment.class);
        OrderItem item = mock(OrderItem.class);

        when(charge.getRefunded()).thenReturn(true);
        when(charge.getPaymentIntent()).thenReturn("pi_123");

        when(paymentRepository.findByPaymentIntent("pi_123")).thenReturn(payment);
        when(payment.getStatus()).thenReturn(PaymentStatus.Succeeded);
        when(payment.getPaymentId()).thenReturn(44);
        when(payment.getOrderId()).thenReturn(12);

        when(orderItemRepository.findByOrderId(12)).thenReturn(List.of(item));
        when(item.getProductId()).thenReturn(9);
        when(item.getQuantity()).thenReturn(2);

        paymentService.processStripeEvent(
                stripeEvent("charge.refunded", charge)
        );

        verify(paymentRepository).updateStatus(44, PaymentStatus.Refunded);
        verify(orderRepository).updateStatus(12, OrderStatus.Refunded);
        verify(productRepository).increaseInventory(9, 2);
    }

    @Test
    void refundOrder_whenPaymentIsNotPaid_rejectsRefund() {
        Payment payment = mock(Payment.class);

        when(paymentRepository.findByOrderId(12)).thenReturn(payment);
        when(payment.getStatus()).thenReturn(PaymentStatus.Failed);

        assertThrows(
                IllegalStateException.class,
                () -> paymentService.refundOrder(12)
        );
    }

    @Test
    void refundOrder_whenStripeSucceeds_marksOrderRefunded() throws StripeException {
        Payment payment = mock(Payment.class);
        Refund stripeRefund = mock(Refund.class);
        OrderItem item = mock(OrderItem.class);

        when(paymentRepository.findByOrderId(12)).thenReturn(payment);
        when(payment.getStatus()).thenReturn(PaymentStatus.Succeeded);
        when(payment.getPaymentIntentId()).thenReturn("pi_123");
        when(payment.getPaymentId()).thenReturn(44);
        when(payment.getOrderId()).thenReturn(12);

        when(stripeRefund.getStatus()).thenReturn("succeeded");

        when(orderItemRepository.findByOrderId(12)).thenReturn(List.of(item));
        when(item.getProductId()).thenReturn(9);
        when(item.getQuantity()).thenReturn(2);

        try (MockedStatic<Refund> refunds = mockStatic(Refund.class)) {
            refunds.when(() -> Refund.create(any(RefundCreateParams.class)))
                    .thenReturn(stripeRefund);

            Refund result = paymentService.refundOrder(12);

            assertEquals(stripeRefund, result);
        }

        verify(paymentRepository).updateStatus(44, PaymentStatus.Refunded);
        verify(orderRepository).updateStatus(12, OrderStatus.Refunded);
        verify(productRepository).increaseInventory(9, 2);
    }

    private Event stripeEvent(String type, StripeObject object) {
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer =
                mock(EventDataObjectDeserializer.class);

        when(event.getType()).thenReturn(type);
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(object));

        return event;
    }
}