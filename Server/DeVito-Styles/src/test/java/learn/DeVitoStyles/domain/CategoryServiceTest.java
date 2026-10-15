package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.CategoryRepository;
import learn.DeVitoStyles.models.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository repository;

    @InjectMocks private CategoryService service;

    @Test
    void shouldFindAll() {
        when(repository.findAll()).thenReturn(List.of(makeCategory(1)));

        Result<List<Category>> result = service.findAll();

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
        assertEquals("Hair Products", result.getpayload().get(0).getName());
    }

    @Test
    void shouldFindAllWhenEmpty() {
        when(repository.findAll()).thenReturn(List.of());

        Result<List<Category>> result = service.findAll();

        assertTrue(result.isSuccess());
        assertTrue(result.getpayload().isEmpty());
    }

    @Test
    void shouldFindById() {
        when(repository.findById(1)).thenReturn(makeCategory(1));

        Result<Category> result = service.findById(1);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getCategoryId());
        assertEquals("Hair Products", result.getpayload().getName());
    }

    @Test
    void shouldNotFindById() {
        when(repository.findById(999)).thenReturn(null);

        Result<Category> result = service.findById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Category with id 999 was not found."), result.getMessages());
        assertNull(result.getpayload());
    }

    private Category makeCategory(int categoryId) {
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setName("Hair Products");
        return category;
    }
}
