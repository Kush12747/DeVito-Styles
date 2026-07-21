package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.Review;

public interface ReviewRepository {

    Review findById(int reviewId);

    Review add(Review review);

    Review findByAppointmentId(int appointmentId);

    boolean hasReviewForAppointment(int appointmentId);
}
