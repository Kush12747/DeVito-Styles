package learn.DeVitoStyles.data;

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
    void add() {
    }

    @Test
    void findByAppointmentId() {
    }
}