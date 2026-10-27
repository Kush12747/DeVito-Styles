package learn.DeVitoStyles.models.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * One line in the dashboard's recent activity feed.
 *
 * type is ORDER, APPOINTMENT or REVIEW; amount is set for orders and
 * appointments, rating for reviews.
 */
public record ActivityEvent(
        String type,
        LocalDateTime occurredAt,
        String customerName,
        String detail,
        String status,
        BigDecimal amount,
        Integer rating) {
}
