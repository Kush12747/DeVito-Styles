package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.CategoryService;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.models.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService service;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CategoryController(service)).build();
    }

    @Test
    void findAll_whenSuccess_returns200WithCategories() throws Exception {
        Result<List<Category>> result = new Result<>();
        result.setpayload(List.of(
                new Category(1, "Hair", "Hair products"),
                new Category(2, "Beard", "Beard products")));
        when(service.findAll()).thenReturn(result);

        mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryId").value(1))
                .andExpect(jsonPath("$[0].name").value("Hair"))
                .andExpect(jsonPath("$[1].description").value("Beard products"));

        verify(service).findAll();
    }

    @Test
    void findAll_whenNotFound_returns404WithEmptyBody() throws Exception {
        Result<List<Category>> result = new Result<>();
        result.addErrorMessage("No categories found", ResultType.NOT_FOUND);
        when(service.findAll()).thenReturn(result);

        mvc.perform(get("/api/categories"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void findById_whenFound_returns200WithCategory() throws Exception {
        Result<Category> result = new Result<>();
        result.setpayload(new Category(2, "Beard", "Beard products"));
        when(service.findById(2)).thenReturn(result);

        mvc.perform(get("/api/categories/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(2))
                .andExpect(jsonPath("$.name").value("Beard"))
                .andExpect(jsonPath("$.description").value("Beard products"));

        verify(service).findById(2);
    }

    @Test
    void findById_whenNotFound_returns404WithEmptyBody() throws Exception {
        Result<Category> result = new Result<>();
        result.addErrorMessage("Category not found", ResultType.NOT_FOUND);
        when(service.findById(99)).thenReturn(result);

        mvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
