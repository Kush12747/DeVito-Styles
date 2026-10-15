package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.ProductService;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.models.Products.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
    }

    private static Product product(int id, String name) {
        return new Product(id, 1, name, "Description of " + name, new BigDecimal("18.99"), 25,
                "https://img/" + id + ".png", true, true,
                LocalDateTime.of(2026, 1, 1, 0, 0), LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    private static <T> Result<T> success(T payload) {
        Result<T> result = new Result<>();
        result.setpayload(payload);
        return result;
    }

    // ---------- GET /{productId} ----------

    @Test
    void findById_whenFound_returns200WithProduct() throws Exception {
        when(productService.findById(4)).thenReturn(success(product(4, "Pomade")));

        mvc.perform(get("/api/products/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(4))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.name").value("Pomade"))
                .andExpect(jsonPath("$.price").value(18.99))
                .andExpect(jsonPath("$.stockQuantity").value(25));

        verify(productService).findById(4);
    }

    @Test
    void findById_whenNotFound_returns404WithEmptyBody() throws Exception {
        Result<Product> result = new Result<>();
        result.addErrorMessage("Product not found", ResultType.NOT_FOUND);
        when(productService.findById(99)).thenReturn(result);

        mvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    // ---------- GET / ----------

    @Test
    void find_withNoParams_usesDefaultSortName_andReturns200() throws Exception {
        when(productService.find(null, null, null, "name"))
                .thenReturn(success(List.of(product(1, "Beard Oil"), product(2, "Pomade"))));

        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Beard Oil"))
                .andExpect(jsonPath("$[1].name").value("Pomade"));

        verify(productService).find(null, null, null, "name");
    }

    @Test
    void find_withAllParams_passesThemToService() throws Exception {
        when(productService.find("oil", 2, true, "price_desc"))
                .thenReturn(success(List.of(product(1, "Beard Oil"))));

        mvc.perform(get("/api/products")
                        .param("keyword", "oil")
                        .param("categoryId", "2")
                        .param("featured", "true")
                        .param("sort", "price_desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productId").value(1));

        verify(productService).find("oil", 2, true, "price_desc");
    }

    @Test
    void find_whenNoMatches_returns200WithEmptyList() throws Exception {
        when(productService.find("zzz", null, null, "name")).thenReturn(success(List.of()));

        mvc.perform(get("/api/products").param("keyword", "zzz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ---------- GET /{productId}/related ----------

    @Test
    void findRelatedProducts_returns200WithList() throws Exception {
        when(productService.findRelatedProducts(4))
                .thenReturn(List.of(product(5, "Hair Clay"), product(6, "Hair Wax")));

        mvc.perform(get("/api/products/4/related"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productId").value(5))
                .andExpect(jsonPath("$[1].name").value("Hair Wax"));

        verify(productService).findRelatedProducts(4);
    }

    @Test
    void findRelatedProducts_whenNone_returns200WithEmptyList() throws Exception {
        when(productService.findRelatedProducts(4)).thenReturn(List.of());

        mvc.perform(get("/api/products/4/related"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
