package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ProductJdbcClientRepositoryTest {

    @Autowired
    private ProductJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindById() {

        Product product = repository.findById(1);

        assertNotNull(product);
        assertEquals(1, product.getProductId());
        assertEquals("Matte Pomade", product.getName());
        assertEquals(1, product.getCategoryId());
    }

    @Test
    void shouldNotFindInvalidId() {

        Product product = repository.findById(999);

        assertNull(product);
    }

    @Test
    void shouldFindAllProducts() {

        List<Product> products = repository.find(
                null,
                null,
                null,
                "name"
        );

        assertNotNull(products);
        assertEquals(3, products.size());
    }

    @Test
    void shouldFindProductsByCategory() {

        List<Product> products = repository.find(
                null,
                1,
                null,
                "name"
        );

        assertEquals(1, products.size());
        assertEquals("Matte Pomade", products.get(0).getName());
    }

    @Test
    void shouldFindFeaturedProducts() {

        List<Product> products = repository.find(
                null,
                null,
                true,
                "name"
        );

        assertEquals(2, products.size());

        assertTrue(products.stream().allMatch(Product::isFeatured));
    }

    @Test
    void shouldSearchProducts() {

        List<Product> products = repository.find(
                "Pomade",
                null,
                null,
                "name"
        );

        assertEquals(1, products.size());
        assertEquals("Matte Pomade", products.get(0).getName());
    }

    @Test
    void shouldReturnEmptyListWhenSearchHasNoMatches() {

        List<Product> products = repository.find(
                "Laptop",
                null,
                null,
                "name"
        );

        assertTrue(products.isEmpty());
    }
}