package learn.DeVitoStyles.data.repositories.orders;

import learn.DeVitoStyles.data.interfaces.orders.PaymentRepository;
import learn.DeVitoStyles.data.mappers.orders.PaymentMapper;
import learn.DeVitoStyles.models.Checkout.Payment;
import learn.DeVitoStyles.models.Checkout.PaymentStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepositoryJdbcClientRepository implements PaymentRepository {

    private final JdbcClient jdbcClient;

    private static final String BASE_SELECT = """
            SELECT
                payment_id,
                order_id,
                payment_provider,
                payment_intent_id,
                payment_status,
                amount,
                currency,
                paid_at,
                created_at
            FROM payments
            """;

    private static final String UPDATE_STATUS = """
            UPDATE payments
            SET
                payment_status = ?,
                paid_at = CURRENT_TIMESTAMP
            WHERE payment_id = ?;
            """;

    public PaymentRepositoryJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Payment add(Payment payment) {

        final String sql = """
                INSERT INTO payments
                (
                    order_id,
                    payment_provider,
                    payment_intent_id,
                    payment_status,
                    amount,
                    currency
                )
                VALUES (?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(payment.getOrderId())
                .param(payment.getPaymentProvider())
                .param(payment.getPaymentIntentId())
                .param(payment.getStatus().name())
                .param(payment.getAmount())
                .param(payment.getCurrency())
                .update(keyHolder, "payment_id");

        if (rowsAffected == 0) {
            return null;
        }

        payment.setPaymentId(keyHolder.getKey().intValue());

        return payment;
    }

    @Override
    public Payment findByOrderId(int orderId) {

        final String sql = BASE_SELECT + """
                WHERE order_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(orderId)
                .query(new PaymentMapper())
                .optional()
                .orElse(null);
    }

    @Override
    public Payment findByPaymentIntentId(String paymentIntentId) {

        final String sql = BASE_SELECT + """
                WHERE payment_intent_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(paymentIntentId)
                .query(new PaymentMapper())
                .optional()
                .orElse(null);
    }

    @Override
    public boolean updateStatus(int paymentId, PaymentStatus paymentStatus) {

        int rowsAffected = jdbcClient.sql(UPDATE_STATUS)
                .param(paymentStatus.name())
                .param(paymentId)
                .update();

        return rowsAffected > 0;
    }
}
