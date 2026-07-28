package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.ProductSpecificationRepository;
import learn.DeVitoStyles.models.Products.ProductSpecification;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ProductSpecificationJdbcRepositoryTest {

    @Autowired
    private ProductSpecificationRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindByProductId() {
        ProductSpecification specification = repository.findByProductId(1);

        assertNotNull(specification);
        assertEquals(1, specification.getProductId());
    }

    @Test
    void shouldNotFindByProductId() {
        ProductSpecification specification = repository.findByProductId(999);
        assertNull(specification);
    }
}