package learn.DeVitoStyles.models.dashboard;

import java.math.BigDecimal;

/** One line of a paid order, for the top products ranking. */
public record OrderItemFact(
        int productId,
        String productName,
        int quantity,
        BigDecimal lineTotal) {
}
