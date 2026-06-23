package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.domain.ReviewService;
import learn.DeVitoStyles.models.Review;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/review")
@CrossOrigin
public class ReviewController {
    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable int id) throws DataAccessException {
        Result<Review> result = service.findById(id);

        if (!result.isSuccess()) {
            return buildErrorResponse(result);
        }
        return ResponseEntity.ok(result.getpayload());
    }

    private ResponseEntity<Object> buildErrorResponse(Result<?> result) {
        if (result.getResultType() == ResultType.NOT_FOUND) {
            return new ResponseEntity<>(result.getMessages(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
    }
}
