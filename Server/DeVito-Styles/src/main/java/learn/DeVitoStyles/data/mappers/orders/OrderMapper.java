package learn.DeVitoStyles.data.mappers.orders;

import learn.DeVitoStyles.models.Checkout.Order;
import learn.DeVitoStyles.models.Checkout.OrderStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {

        Order order = new Order();

        order.setOrderId(rs.getInt("order_id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setUserId(rs.getInt("user_id"));
        order.setStatus(
                OrderStatus.valueOf(rs.getString("status"))
        );
        order.setSubtotal(rs.getBigDecimal("subtotal"));
        order.setTaxAmount(rs.getBigDecimal("tax_amount"));
        order.setShippingCost(rs.getBigDecimal("shipping_cost"));
        order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));

        if (rs.getTimestamp("created_at") != null) {
            order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        if (rs.getTimestamp("updated_at") != null) {
            order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return order;
    }
}
