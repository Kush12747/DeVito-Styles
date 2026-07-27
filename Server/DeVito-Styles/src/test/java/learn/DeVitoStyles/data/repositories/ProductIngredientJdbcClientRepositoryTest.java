package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.ProductIngredientRepository;
import learn.DeVitoStyles.models.Products.ProductIngredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ProductIngredientJdbcRepositoryTest {

    @Autowired
    private ProductIngredientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindByProductId() {

        List<ProductIngredient> ingredients = repository.findByProductId(1);

        assertNotNull(ingredients);
        assertEquals(5, ingredients.size());

        assertEquals("Beeswax", ingredients.get(0).getIngredient());
    }

    @Test
    void shouldNotFindByProductId() {

        List<ProductIngredient> ingredients = repository.findByProductId(999);

        assertNotNull(ingredients);
        assertTrue(ingredients.isEmpty());
    }
}