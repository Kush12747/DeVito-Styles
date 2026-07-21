package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.ProductRepository;
import learn.DeVitoStyles.models.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ProductServiceTest {

    @Autowired
    ProductService service;

    @MockBean
    ProductRepository repository;


    @Test
    void shouldFindById() {

        Product product = new Product();
        product.setProductId(1);
        product.setName("Hair Gel");
        product.setPrice(new BigDecimal("12.99"));

        when(repository.findById(1)).thenReturn(product);

        Result<Product> result = service.findById(1);

        assertTrue(result.isSuccess());
        assertEquals("Hair Gel", result.getpayload().getName());
        assertEquals(1, result.getpayload().getProductId());
    }


    @Test
    void shouldNotFindById() {

        when(repository.findById(999)).thenReturn(null);

        Result<Product> result = service.findById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }


    @Test
    void shouldFindProducts() {

        Product product = new Product();
        product.setProductId(1);
        product.setName("Hair Gel");

        when(repository.find(
                "gel",
                null,
                null,
                "priceAsc"
        )).thenReturn(List.of(product));


        Result<List<Product>> result = service.find(
                "gel",
                null,
                null,
                "priceAsc"
        );


        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
        assertEquals("Hair Gel", result.getpayload().get(0).getName());
    }


    @Test
    void shouldReturnEmptyListWhenNoProductsMatch() {

        when(repository.find(
                "unknown",
                null,
                null,
                "name"
        )).thenReturn(List.of());


        Result<List<Product>> result = service.find(
                "unknown",
                null,
                null,
                "name"
        );


        assertTrue(result.isSuccess());
        assertTrue(result.getpayload().isEmpty());
    }


    @Test
    void shouldFindFeaturedProductsByCategory() {

        Product product = new Product();
        product.setProductId(1);
        product.setName("Premium Shampoo");

        when(repository.find(
                null,
                2,
                true,
                "priceDesc"
        )).thenReturn(List.of(product));


        Result<List<Product>> result = service.find(
                null,
                2,
                true,
                "priceDesc"
        );


        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
        assertEquals("Premium Shampoo", result.getpayload().get(0).getName());
    }
}