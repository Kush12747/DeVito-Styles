package learn.DeVitoStyles.data;

import learn.DeVitoStyles.data.mappers.ReviewMapper;
import learn.DeVitoStyles.models.Review;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewJdbcClientRepository implements ReviewRepository {
    private final JdbcClient jdbcClient;
    private final String BASE_SELECT = "SELECT review_id, user_id, barber_id, appointment_id, rating, review_text, created_at FROM review";

    public ReviewJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // Get a review
    @Override
    public Review findById(int reviewId) {
        final String sql = BASE_SELECT + " WHERE review_id = ?;";

        return jdbcClient.sql(sql)
                .param(reviewId)
                .query(new ReviewMapper())
                .optional()
                .orElse(null);
    }

    // Add a review
    @Override
    public Review add(Review review) {
        final String sql = """
                INSERT INTO review (user_id, barber_id, appointment_id, rating, review_text)
                VALUES
                (?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(review.getUserId())
                .param(review.getBarberId())
                .param(review.getAppointmentId())
                .param(review.getRating())
                .param(review.getReviewText())
                .update(keyHolder, "review_id");

        if (rowsAffected == 0) {
            return null;
        }

        review.setReviewId(keyHolder.getKey().intValue());
        return review;
    }

    // Prevent duplicate reviews
    @Override
    public Review findByAppointmentId(int appointmentId) {
        final String sql = BASE_SELECT + " WHERE appointment_id = ?";

        return jdbcClient.sql(sql)
                .param(appointmentId)
                .query(new ReviewMapper())
                .optional()
                .orElse(null);
    }
}
