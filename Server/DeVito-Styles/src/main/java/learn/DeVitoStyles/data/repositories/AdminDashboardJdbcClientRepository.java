package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.AdminDashboardRepository;
import learn.DeVitoStyles.models.dashboard.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Repository
public class AdminDashboardJdbcClientRepository implements AdminDashboardRepository {

    // Orders in these states count as money in the till
    private static final String PAID_ORDER_STATUSES = "('Paid', 'Processing', 'Completed')";

    private final JdbcClient jdbcClient;

    public AdminDashboardJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<AppointmentFact> findAppointments(LocalDateTime start, LocalDateTime end) {
        final String sql = """
                SELECT a.appointment_id, a.appointment_datetime, a.status, a.user_id,
                       a.service_id, s.name AS service_name, s.price AS service_price,
                       a.barber_id, CONCAT(b.first_name, ' ', b.last_name) AS barber_name
                FROM appointment a
                JOIN service s ON s.service_id = a.service_id
                JOIN barber b ON b.barber_id = a.barber_id
                WHERE a.appointment_datetime >= ? AND a.appointment_datetime < ?;
                """;

        return jdbcClient.sql(sql)
                .param(start)
                .param(end)
                .query(AppointmentFact.class)
                .list();
    }

    @Override
    public List<OrderFact> findOrders(LocalDateTime start, LocalDateTime end) {
        final String sql = """
                SELECT order_id, created_at, status, total_amount
                FROM orders
                WHERE created_at >= ? AND created_at < ?;
                """;

        return jdbcClient.sql(sql)
                .param(start)
                .param(end)
                .query(OrderFact.class)
                .list();
    }

    @Override
    public List<OrderItemFact> findPaidOrderItems(LocalDateTime start, LocalDateTime end) {
        final String sql = """
                SELECT oi.product_id, oi.product_name, oi.quantity, oi.line_total
                FROM order_items oi
                JOIN orders o ON o.order_id = oi.order_id
                WHERE o.status IN %s
                  AND o.created_at >= ? AND o.created_at < ?;
                """.formatted(PAID_ORDER_STATUSES);

        return jdbcClient.sql(sql)
                .param(start)
                .param(end)
                .query(OrderItemFact.class)
                .list();
    }

    @Override
    public List<ReviewFact> findReviews(LocalDateTime start, LocalDateTime end) {
        final String sql = """
                SELECT created_at, barber_id, rating
                FROM review
                WHERE created_at >= ? AND created_at < ?;
                """;

        return jdbcClient.sql(sql)
                .param(start)
                .param(end)
                .query(ReviewFact.class)
                .list();
    }

    @Override
    public List<UpcomingAppointment> findUpcomingAppointments(LocalDateTime from, int limit) {
        final String sql = """
                SELECT a.appointment_id, a.appointment_datetime,
                       CONCAT(u.first_name, ' ', u.last_name) AS customer_name,
                       s.name AS service_name,
                       CONCAT(b.first_name, ' ', b.last_name) AS barber_name
                FROM appointment a
                JOIN users u ON u.user_id = a.user_id
                JOIN service s ON s.service_id = a.service_id
                JOIN barber b ON b.barber_id = a.barber_id
                WHERE a.status = 'BOOKED' AND a.appointment_datetime >= ?
                ORDER BY a.appointment_datetime
                LIMIT ?;
                """;

        return jdbcClient.sql(sql)
                .param(from)
                .param(limit)
                .query(UpcomingAppointment.class)
                .list();
    }

    // Latest orders, past appointments and reviews, merged newest first
    @Override
    public List<ActivityEvent> findRecentActivity(LocalDateTime before, int limit) {
        final String ordersSql = """
                SELECT 'ORDER' AS type, o.created_at AS occurred_at,
                       CONCAT(u.first_name, ' ', u.last_name) AS customer_name,
                       o.order_number AS detail, o.status, o.total_amount AS amount, NULL AS rating
                FROM orders o
                JOIN users u ON u.user_id = o.user_id
                WHERE o.created_at < ?
                ORDER BY o.created_at DESC
                LIMIT ?;
                """;

        final String appointmentsSql = """
                SELECT 'APPOINTMENT' AS type, a.appointment_datetime AS occurred_at,
                       CONCAT(u.first_name, ' ', u.last_name) AS customer_name,
                       CONCAT(s.name, ' with ', b.first_name) AS detail, a.status,
                       s.price AS amount, NULL AS rating
                FROM appointment a
                JOIN users u ON u.user_id = a.user_id
                JOIN service s ON s.service_id = a.service_id
                JOIN barber b ON b.barber_id = a.barber_id
                WHERE a.appointment_datetime < ?
                ORDER BY a.appointment_datetime DESC
                LIMIT ?;
                """;

        final String reviewsSql = """
                SELECT 'REVIEW' AS type, r.created_at AS occurred_at,
                       CONCAT(u.first_name, ' ', u.last_name) AS customer_name,
                       CONCAT('Reviewed ', b.first_name) AS detail, NULL AS status,
                       NULL AS amount, r.rating
                FROM review r
                JOIN users u ON u.user_id = r.user_id
                JOIN barber b ON b.barber_id = r.barber_id
                WHERE r.created_at < ?
                ORDER BY r.created_at DESC
                LIMIT ?;
                """;

        List<ActivityEvent> events = new ArrayList<>();
        for (String sql : List.of(ordersSql, appointmentsSql, reviewsSql)) {
            events.addAll(jdbcClient.sql(sql)
                    .param(before)
                    .param(limit)
                    .query(ActivityEvent.class)
                    .list());
        }

        return events.stream()
                .sorted(Comparator.comparing(ActivityEvent::occurredAt).reversed())
                .limit(limit)
                .toList();
    }
}
