package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.AppointmentRepository;
import learn.DeVitoStyles.data.interfaces.ReviewRepository;
import learn.DeVitoStyles.models.Appointment;
import learn.DeVitoStyles.models.Review;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewRepository repository;
    private final AppointmentRepository appointmentRepository;

    public ReviewService(ReviewRepository repository, AppointmentRepository appointmentRepository) {
        this.repository = repository;
        this.appointmentRepository = appointmentRepository;
    }

    public Result<Review> findById(int reviewId) {
        Result<Review> result = new Result<>();

        if (reviewId <= 0) {
            result.addErrorMessage("Review ID must be greater than 0.", ResultType.INVALID);
            return result;
        }

        Review review = repository.findById(reviewId);

        if (review == null) {
            result.addErrorMessage("Review not found", ResultType.NOT_FOUND);
            return result;
        }

        result.setpayload(review);
        return result;
    }

    public Result<Review> add(Review review) {

        Result<Review> result = validate(review);

        if (!result.isSuccess()) {
            return result;
        }

        Review existing =
                repository.findByAppointmentId(review.getAppointmentId());

        if (existing != null) {
            result.addErrorMessage(
                    "Review already exists for appointment",
                    ResultType.CONFLICT);
            return result;
        }

        Appointment appointment =
                appointmentRepository.findById(review.getAppointmentId());

        if (appointment == null) {
            result.addErrorMessage(
                    "Appointment not found",
                    ResultType.NOT_FOUND);
            return result;
        }

        if (!appointment.getStatus().equalsIgnoreCase("COMPLETED")) {
            result.addErrorMessage(
                    "Only completed appointments can be reviewed",
                    ResultType.INVALID);
            return result;
        }

        if (appointment.getUserId() != review.getUserId()) {
            result.addErrorMessage(
                    "User does not match appointment",
                    ResultType.CONFLICT);
            return result;
        }

        Review posted = repository.add(review);
        result.setpayload(posted);

        return result;
    }

    private Result<Review> validate(Review review) {
        Result<Review> result = new Result<>();

        if (review == null) {
            result.addErrorMessage("Review can't be null", ResultType.INVALID);
            return result;
        }

        if (review.getUserId() <= 0) {
            result.addErrorMessage("User is required", ResultType.INVALID);
            return result;
        }

        if (review.getAppointmentId() <= 0) {
            result.addErrorMessage("Appointment id is required", ResultType.INVALID);
            return result;
        }

        if (review.getBarberId() <= 0) {
            result.addErrorMessage("Barber id is required", ResultType.INVALID);
            return result;
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            result.addErrorMessage("Rating must be between 1 and 5", ResultType.INVALID);
            return result;
        }

        if (review.getReviewText() != null && review.getReviewText().length() > 1000) {
            result.addErrorMessage("Review must be less than 1000 characters", ResultType.INVALID);
            return result;
        }

        return result;
    }
}
