package learn.DeVitoStyles.data.repositories.orders;

import learn.DeVitoStyles.models.Checkout.Order;
import learn.DeVitoStyles.models.Checkout.OrderStatus;
import learn.DeVitoStyles.models.Checkout.Payment;
import learn.DeVitoStyles.models.Checkout.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class PaymentRepositoryJdbcClientRepositoryTest {

    @Autowired
    private PaymentRepositoryJdbcClientRepository repository;

    @Autowired
    private OrderJdbcClientRepository orderRepository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void add() {

        Order order = orderRepository.add(makeOrder());

        Payment payment = makePayment();
        payment.setOrderId(order.getOrderId());

        Payment actual = repository.add(payment);

        assertNotNull(actual);
        assertTrue(actual.getPaymentId() > 0);
        assertEquals(order.getOrderId(), actual.getOrderId());
    }

    @Test
    void findByOrderId() {

        Order order = orderRepository.add(makeOrder());

        Payment payment = makePayment();
        payment.setOrderId(order.getOrderId());

        repository.add(payment);

        Payment actual = repository.findByOrderId(order.getOrderId());

        assertNotNull(actual);
        assertEquals(order.getOrderId(), actual.getOrderId());
    }

    @Test
    void findByPaymentIntentId() {

        Order order = orderRepository.add(makeOrder());

        Payment payment = makePayment();
        payment.setOrderId(order.getOrderId());

        repository.add(payment);

        Payment actual = repository.findByPaymentIntentId(payment.getPaymentIntentId());

        assertNotNull(actual);
        assertEquals(payment.getPaymentIntentId(), actual.getPaymentIntentId());
    }

    @Test
    void updateStatus() {

        Order order = orderRepository.add(makeOrder());

        Payment payment = makePayment();
        payment.setOrderId(order.getOrderId());

        Payment saved = repository.add(payment);

        assertTrue(
                repository.updateStatus(
                        saved.getPaymentId(),
                        PaymentStatus.valueOf(PaymentStatus.Succeeded.name())
                )
        );

        Payment updated = repository.findByOrderId(order.getOrderId());

        assertEquals(PaymentStatus.Succeeded, updated.getStatus());
    }

    private Order makeOrder() {

        Order order = new Order();

        order.setOrderNumber("DEV-100001");
        order.setUserId(2);
        order.setStatus(OrderStatus.Pending);
        order.setSubtotal(new BigDecimal("39.98"));
        order.setTaxAmount(new BigDecimal("3.20"));
        order.setShippingCost(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTotalAmount(new BigDecimal("43.18"));

        return order;
    }

    private Payment makePayment() {

        Payment payment = new Payment();

        payment.setPaymentProvider("Stripe");
        payment.setPaymentIntentId("pi_test_123456789");
        payment.setStatus(PaymentStatus.Pending);
        payment.setAmount(new BigDecimal("43.18"));
        payment.setCurrency("USD");

        return payment;
    }
}