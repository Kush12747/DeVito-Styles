package learn.DeVitoStyles.data.repositories.orders;

import learn.DeVitoStyles.data.interfaces.orders.OrderItemRepository;
import learn.DeVitoStyles.data.mappers.orders.OrderItemMapper;
import learn.DeVitoStyles.models.Checkout.OrderItem;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemRepositoryJdbcClient implements OrderItemRepository {

    private final JdbcClient jdbcClient;
    private final JdbcTemplate jdbcTemplate;

    private static final String BASE_SELECT = """
            SELECT
                order_item_id,
                order_id,
                product_id,
                product_name,
                quantity,
                unit_price,
                line_total
            FROM order_items
            """;

    public OrderItemRepositoryJdbcClient(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
        this.jdbcClient = jdbcClient;
        this.jdbcTemplate = jdbcTemplate;
    }

    /*
        Adds a single item to an order
        Used after creating an order
     */
    @Override
    public OrderItem add(OrderItem orderItem) {

        final String sql = """
                INSERT INTO order_items
                (
                    order_id,
                    product_id,
                    product_name,
                    quantity,
                    unit_price,
                    line_total
                )
                VALUES (?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(orderItem.getOrderId())
                .param(orderItem.getProductId())
                .param(orderItem.getProductName())
                .param(orderItem.getQuantity())
                .param(orderItem.getUnitPrice())
                .param(orderItem.getLineTotal())
                .update(keyHolder, "order_item_id");


        if(rowsAffected == 0){
            return null;
        }

        orderItem.setOrderItemId(keyHolder.getKey().intValue());

        return orderItem;
    }

    /*
        Gets all products belonging to an order.
        Used for:
            - Order details page
            - Payment confirmation
            - Order history
     */
    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        String sql = BASE_SELECT + """
                WHERE order_id = ?;
                """;


        return jdbcClient.sql(sql)
                .param(orderId)
                .query(new OrderItemMapper())
                .list();
    }

    @Override
    public boolean addAll(List<OrderItem> orderItems) {

        if (orderItems == null || orderItems.isEmpty()) {
            return false;
        }

        for (OrderItem orderItem : orderItems) {
            OrderItem added = add(orderItem);

            if (added == null) {
                return false;
            }
        }

        return true;
    }
}
