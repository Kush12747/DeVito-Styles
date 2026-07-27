package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.ProductUsageStepRepository;
import learn.DeVitoStyles.models.Products.ProductUsageStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ProductUsageStepJdbcRepositoryTest {

    @Autowired
    private ProductUsageStepRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindByProductId() {

        List<ProductUsageStep> steps = repository.findByProductId(1);

        assertNotNull(steps);
        assertEquals(4, steps.size());

        assertEquals(1, steps.get(0).getStepNumber());

        assertEquals(
                "Scoop a small amount onto your fingertips.",
                steps.get(0).getInstruction()
        );
    }

    @Test
    void shouldNotFindByProductId() {

        List<ProductUsageStep> steps = repository.findByProductId(999);

        assertNotNull(steps);
        assertTrue(steps.isEmpty());
    }
}