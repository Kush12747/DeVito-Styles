package learn.DeVitoStyles.data.mappers.orders;

import learn.DeVitoStyles.models.Checkout.Payment;
import learn.DeVitoStyles.models.Checkout.PaymentStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentMapper implements RowMapper<Payment> {

    @Override
    public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {

        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setOrderId(rs.getInt("order_id"));
        payment.setPaymentProvider(rs.getString("payment_provider"));
        payment.setPaymentIntentId(rs.getString("payment_intent_id"));
        payment.setStatus(PaymentStatus.valueOf(rs.getString("payment_status")));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setCurrency(rs.getString("currency"));

        if (rs.getTimestamp("paid_at") != null) {
            payment.setPaidAt(
                    rs.getTimestamp("paid_at").toLocalDateTime()
            );
        }

        var createdTimestamp = rs.getTimestamp("created_at");

        if (createdTimestamp != null) {
            payment.setCreatedAt(createdTimestamp.toLocalDateTime());
        }

        return payment;
    }
}
