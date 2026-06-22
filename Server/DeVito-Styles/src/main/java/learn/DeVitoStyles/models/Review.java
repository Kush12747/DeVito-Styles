package learn.DeVitoStyles.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Review {
    private int reviewId;
    private int userId;
    private int appointmentId;
    private int barberId;
    private int rating;
    private String reviewText;
    private LocalDateTime createdAt;

    public Review() {
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getBarberId() {
        return barberId;
    }

    public void setBarberId(int barberId) {
        this.barberId = barberId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return reviewId == review.reviewId && userId == review.userId && appointmentId == review.appointmentId && barberId == review.barberId && rating == review.rating && Objects.equals(reviewText, review.reviewText) && Objects.equals(createdAt, review.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, userId, appointmentId, barberId, rating, reviewText, createdAt);
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", userId=" + userId +
                ", appointmentId=" + appointmentId +
                ", barberId=" + barberId +
                ", rating=" + rating +
                ", reviewText='" + reviewText + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
