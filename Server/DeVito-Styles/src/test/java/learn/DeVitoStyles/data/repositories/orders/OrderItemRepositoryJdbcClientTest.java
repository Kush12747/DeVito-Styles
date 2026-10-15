package learn.DeVitoStyles.data.repositories.orders;

import learn.DeVitoStyles.models.Checkout.Order;
import learn.DeVitoStyles.models.Checkout.OrderItem;
import learn.DeVitoStyles.models.Checkout.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class OrderItemRepositoryJdbcClientTest {

    @Autowired
    private OrderItemRepositoryJdbcClient repository;

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

        OrderItem item = makeOrderItem();
        item.setOrderId(order.getOrderId());

        OrderItem actual = repository.add(item);

        assertNotNull(actual);
        assertTrue(item.getOrderItemId() > 0);
        assertEquals(order.getOrderId(), item.getOrderId());
    }

    @Test
    void findByOrderId() {

        Order order = orderRepository.add(makeOrder());

        OrderItem item1 = makeOrderItem();
        item1.setOrderId(order.getOrderId());
        repository.add(item1);

        OrderItem item2 = makeSecondOrderItem();
        item2.setOrderId(order.getOrderId());
        repository.add(item2);

        List<OrderItem> items = repository.findByOrderId(order.getOrderId());

        assertNotNull(items);
        assertEquals(2, items.size());

        assertTrue(
                items.stream()
                        .allMatch(i -> i.getOrderId() == order.getOrderId())
        );
    }

    @Test
    void addAll() {

        Order order = orderRepository.add(makeOrder());

        OrderItem item1 = makeOrderItem();
        item1.setOrderId(order.getOrderId());

        OrderItem item2 = makeSecondOrderItem();
        item2.setOrderId(order.getOrderId());

        repository.addAll(List.of(item1, item2));

        List<OrderItem> items = repository.findByOrderId(order.getOrderId());

        assertEquals(2, items.size());
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

    private OrderItem makeOrderItem() {

        OrderItem item = new OrderItem();

        item.setProductId(1);
        item.setProductName("Matte Pomade");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("19.99"));
        item.setLineTotal(new BigDecimal("39.98"));

        return item;
    }

    private OrderItem makeSecondOrderItem() {

        OrderItem item = new OrderItem();

        item.setProductId(2);
        item.setProductName("Daily Shampoo");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("14.99"));
        item.setLineTotal(new BigDecimal("14.99"));

        return item;
    }
}