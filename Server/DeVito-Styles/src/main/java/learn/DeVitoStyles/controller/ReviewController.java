package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ReviewService;
import learn.DeVitoStyles.models.Review;
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
    public ResponseEntity<Object> findById(@PathVariable int id) {
        Result<Review> result = service.findById(id);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        return ResponseEntity.ok(result.getpayload());
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Review review)  {
        Result<Review> result = service.add(review);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getpayload(), HttpStatus.CREATED);
    }
}
