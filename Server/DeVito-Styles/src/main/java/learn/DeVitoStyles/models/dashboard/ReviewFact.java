package learn.DeVitoStyles.models.dashboard;

import java.time.LocalDateTime;

/** One review's rating, for average rating stats. */
public record ReviewFact(
        LocalDateTime createdAt,
        int barberId,
        int rating) {
}
