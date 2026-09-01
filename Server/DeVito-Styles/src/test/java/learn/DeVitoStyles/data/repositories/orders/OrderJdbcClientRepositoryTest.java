package learn.DeVitoStyles.data.repositories.orders;

import learn.DeVitoStyles.models.Checkout.Order;
import learn.DeVitoStyles.models.Checkout.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class OrderJdbcClientRepositoryTest {

    @Autowired
    private OrderJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void add() {

        Order order = makeOrder();

        Order actual = repository.add(order);

        assertNotNull(actual);
        assertTrue(actual.getOrderId() > 0);
        assertEquals("DEV-100001", actual.getOrderNumber());
    }

    @Test
    void findById() {

        Order expected = repository.add(makeOrder());

        Order actual = repository.findById(expected.getOrderId());

        assertNotNull(actual);
        assertEquals(expected.getOrderId(), actual.getOrderId());
    }

    @Test
    void findByOrderNumber() {

        Order expected = repository.add(makeOrder());

        Order actual = repository.findByOrderNumber(expected.getOrderNumber());

        assertNotNull(actual);
        assertEquals(expected.getOrderNumber(), actual.getOrderNumber());
    }

    @Test
    void updateStatus() {

        Order order = repository.add(makeOrder());

        assertTrue(
                repository.updateStatus(
                        order.getOrderId(),
                        OrderStatus.Paid
                )
        );

        Order updated = repository.findById(order.getOrderId());

        assertEquals(OrderStatus.Paid, updated.getStatus());
    }

    @Test
    void findByUserId() {

        repository.add(makeOrder("DEV-100001"));
        repository.add(makeOrder("DEV-100002"));

        Order third = makeOrder("DEV-100003");
        third.setUserId(3);
        repository.add(third);

        var orders = repository.findByUserId(2);

        assertNotNull(orders);
        assertEquals(3, orders.size());

        assertTrue(
                orders.stream()
                        .allMatch(o -> o.getUserId() == 2)
        );
    }

    @Test
    void shouldNotFindMissingOrderById() {

        Order actual = repository.findById(999);

        assertNull(actual);
    }

    @Test
    void shouldNotFindMissingOrderNumber() {

        Order actual = repository.findByOrderNumber("DOES-NOT-EXIST");

        assertNull(actual);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoOrders() {

        var orders = repository.findByUserId(999);

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    void shouldReturnFalseWhenUpdatingMissingOrder() {

        assertFalse(repository.updateStatus(999, OrderStatus.Paid));
    }

    private Order makeOrder(String orderNumber) {

        Order order = makeOrder();
        order.setOrderNumber(orderNumber);

        return order;
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
}