package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.models.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ReviewJdbcClientRepositoryTest {

    @Autowired
    private ReviewJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindReviewById() {
        Review review = repository.findById(1);

        assertNotNull(review);
        assertEquals(1, review.getReviewId());
    }

    @Test
    void shouldNotFindReviewById() {
        Review review = repository.findById(9999);
        assertNull(review);
    }

    @Test
    void shouldAddReview() {
        Review review = new Review();

        review.setUserId(2);
        review.setBarberId(1);
        review.setAppointmentId(4);
        review.setRating(4);
        review.setReviewText("Great Service.");

        Review result = repository.add(review);

        assertNotNull(result);
        assertTrue(result.getReviewId() > 0);

        Review fromDb = repository.findById(result.getReviewId());
        assertEquals("Great Service.", fromDb.getReviewText());
    }

    @Test
    void shouldFindReviewByAppointmentId() {
        Review review = repository.findByAppointmentId(1);

        assertNotNull(review);
        assertEquals(1, review.getAppointmentId());
    }

    @Test
    void shouldNotFindReviewByAppointmentId() {
        Review review = repository.findByAppointmentId(9999);
        assertNull(review);
    }
}