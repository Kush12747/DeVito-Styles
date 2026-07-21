package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.models.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CategoryJdbcClientRepositoryTest {

    @Autowired
    private CategoryJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindAll() {

        List<Category> categories = repository.findAll();

        assertNotNull(categories);
        assertFalse(categories.isEmpty());
    }

    @Test
    void shouldFindById() {

        Category category = repository.findById(1);

        assertNotNull(category);
        assertEquals(1, category.getCategoryId());
    }

    @Test
    void shouldNotFindInvalidId() {

        Category category = repository.findById(999);

        assertNull(category);
    }
}