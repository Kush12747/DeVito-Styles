package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.ProductBenefitRepository;
import learn.DeVitoStyles.models.Products.ProductBenefit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ProductBenefitJdbcRepositoryTest {

    @Autowired
    private ProductBenefitRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindByProductId() {

        List<ProductBenefit> benefits = repository.findByProductId(1);

        assertNotNull(benefits);
        assertEquals(5, benefits.size());

        assertEquals("Provides Medium Hold", benefits.get(0).getBenefit());
    }

    @Test
    void shouldNotFindByProductId() {

        List<ProductBenefit> benefits = repository.findByProductId(999);

        assertNotNull(benefits);
        assertTrue(benefits.isEmpty());
    }
}