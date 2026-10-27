package learn.DeVitoStyles.models.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** One product order's headline numbers, for dashboard math. */
public record OrderFact(
        int orderId,
        LocalDateTime createdAt,
        String status,
        BigDecimal totalAmount) {
}
