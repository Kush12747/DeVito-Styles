package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.CategoryRepository;
import learn.DeVitoStyles.models.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CategoryServiceTest {

    @Autowired
    CategoryService service;

    @MockBean
    CategoryRepository repository;


    @Test
    void shouldFindAll() {
        Category category = new Category();
        category.setCategoryId(1);
        category.setName("Hair Products");

        when(repository.findAll()).thenReturn(List.of(category));

        Result<List<Category>> result = service.findAll();

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
        assertEquals("Hair Products", result.getpayload().get(0).getName());
    }


    @Test
    void shouldFindById() {
        Category category = new Category();
        category.setCategoryId(1);
        category.setName("Hair Products");

        when(repository.findById(1)).thenReturn(category);

        Result<Category> result = service.findById(1);

        assertTrue(result.isSuccess());
        assertEquals("Hair Products", result.getpayload().getName());
    }


    @Test
    void shouldNotFindById() {
        when(repository.findById(999)).thenReturn(null);

        Result<Category> result = service.findById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }
}