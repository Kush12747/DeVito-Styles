package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.ReviewRepository;
import learn.DeVitoStyles.models.Review;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
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
}
