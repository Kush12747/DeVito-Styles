package learn.DeVitoStyles.data.repositories.orders;

import learn.DeVitoStyles.data.interfaces.orders.OrderRepository;
import learn.DeVitoStyles.data.mappers.orders.OrderMapper;
import learn.DeVitoStyles.models.Checkout.Order;
import learn.DeVitoStyles.models.Checkout.OrderStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderJdbcClientRepository implements OrderRepository {

    private final JdbcClient jdbcClient;

    private static final String BASE_SELECT = """
        SELECT
            order_id,
            order_number,
            user_id,
            status,
            subtotal,
            tax_amount,
            shipping_cost,
            discount_amount,
            total_amount,
            created_at,
            updated_at
        FROM orders
        """;

    private static final String UPDATE_STATUS = """
        UPDATE orders
        SET
            status = ?
        WHERE order_id = ?;
        """;

    private static final String FIND_BY_USER = BASE_SELECT + """
        WHERE user_id = ?
        ORDER BY created_at DESC;
        """;

    public OrderJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /*
        Create the order before talking to Stripe.
        Returns the generated OrderId.
     */
    @Override
    public Order add(Order order) {

        final String sql = """
            INSERT INTO orders
            (
                order_number,
                user_id,
                status,
                subtotal,
                tax_amount,
                shipping_cost,
                discount_amount,
                total_amount
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcClient.sql(sql)
                .param(order.getOrderNumber())
                .param(order.getUserId())
                .param(order.getStatus().name())
                .param(order.getSubtotal())
                .param(order.getTaxAmount())
                .param(order.getShippingCost())
                .param(order.getDiscountAmount())
                .param(order.getTotalAmount())
                .update(keyHolder, "order_id");

        if (rowsAffected == 0) {
            return null;
        }

        order.setOrderId(keyHolder.getKey().intValue());
        return order;
    }

    /*
        We need this when:
            - Retrieving an order
            - displaying an order confirmation
            - verifying ownership
     */
    @Override
    public Order findById(int orderId) {
        final String sql = BASE_SELECT + " WHERE order_id = ?;";

        return jdbcClient.sql(sql)
                .param(orderId)
                .query(new OrderMapper())
                .optional()
                .orElse(null);
    }


    /*
        Useful because customers usually see: DEV-100024
        instead of 15
     */
    @Override
    public Order findByOrderNumber(String orderNumber) {
        final String sql = BASE_SELECT + " WHERE order_number = ?;";

        return jdbcClient.sql(sql)
                .param(orderNumber)
                .query(new OrderMapper())
                .optional()
                .orElse(null);
    }

    /*
        Used after payment succeeds.
        ex: pending, paid, processing, completed
     */
    @Override
    public boolean updateStatus(int orderId, OrderStatus status) {

        int rowsAffected = jdbcClient.sql(UPDATE_STATUS)
                .param(status.name())
                .param(orderId)
                .update();

        return rowsAffected> 0;
    }

    /*
        Examples:

        Customer order history
        Profile page
        Admin viewing customer purchases
     */
    @Override
    public List<Order> findByUserId(int userId) {

        return jdbcClient.sql(FIND_BY_USER)
                .param(userId)
                .query(new OrderMapper())
                .list();
    }
}
