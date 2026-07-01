package learn.DeVitoStyles.data.mappers;

import learn.DeVitoStyles.models.Review;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ReviewMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();

        review.setReviewId(rs.getInt("review_id"));
        review.setUserId(rs.getInt("user_id"));
        review.setBarberId(rs.getInt("barber_id"));
        review.setAppointmentId(rs.getInt("appointment_id"));
        review.setRating(rs.getInt("rating"));
        review.setReviewText(rs.getString("review_text"));

        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            review.setCreatedAt(timestamp.toLocalDateTime());
        }

        return review;
    }
}
