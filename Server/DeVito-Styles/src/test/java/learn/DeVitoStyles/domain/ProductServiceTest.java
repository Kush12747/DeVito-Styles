package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.*;
import learn.DeVitoStyles.models.Products.*;
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

    @MockBean
    ProductSpecificationRepository specificationRepository;

    @MockBean
    ProductBenefitRepository benefitRepository;

    @MockBean
    ProductIngredientRepository ingredientRepository;

    @MockBean
    ProductUsageStepRepository usageStepRepository;

    @Test
    void shouldFindById() {

        Product product = new Product();
        product.setProductId(1);
        product.setName("Hair Gel");
        product.setPrice(new BigDecimal("12.99"));

        ProductSpecification specification = new ProductSpecification();
        specification.setProductId(1);
        specification.setSize("4 oz");

        ProductBenefit benefit = new ProductBenefit();
        benefit.setBenefit("Strong Hold");

        ProductIngredient ingredient = new ProductIngredient();
        ingredient.setIngredient("Beeswax");

        ProductUsageStep step = new ProductUsageStep();
        step.setStepNumber(1);
        step.setInstruction("Apply to dry hair.");

        when(repository.findById(1)).thenReturn(product);

        when(specificationRepository.findByProductId(1))
                .thenReturn(specification);

        when(benefitRepository.findByProductId(1))
                .thenReturn(List.of(benefit));

        when(ingredientRepository.findByProductId(1))
                .thenReturn(List.of(ingredient));

        when(usageStepRepository.findByProductId(1))
                .thenReturn(List.of(step));

        Result<Product> result = service.findById(1);

        assertTrue(result.isSuccess());

        Product payload = result.getpayload();

        assertEquals("Hair Gel", payload.getName());
        assertEquals(1, payload.getProductId());

        assertNotNull(payload.getSpecification());
        assertEquals("4 oz", payload.getSpecification().getSize());

        assertEquals(1, payload.getBenefits().size());
        assertEquals("Strong Hold", payload.getBenefits().get(0).getBenefit());

        assertEquals(1, payload.getIngredients().size());
        assertEquals("Beeswax", payload.getIngredients().get(0).getIngredient());

        assertEquals(1, payload.getUsageSteps().size());
        assertEquals(
                "Apply to dry hair.",
                payload.getUsageSteps().get(0).getInstruction()
        );
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

    @Test
    void shouldPopulateProductDetails() {

        Product product = new Product();
        product.setProductId(1);

        when(repository.findById(1)).thenReturn(product);

        when(specificationRepository.findByProductId(1))
                .thenReturn(new ProductSpecification());

        when(benefitRepository.findByProductId(1))
                .thenReturn(List.of(new ProductBenefit()));

        when(ingredientRepository.findByProductId(1))
                .thenReturn(List.of(new ProductIngredient()));

        when(usageStepRepository.findByProductId(1))
                .thenReturn(List.of(new ProductUsageStep()));

        Result<Product> result = service.findById(1);

        assertTrue(result.isSuccess());

        Product payload = result.getpayload();

        assertNotNull(payload.getSpecification());
        assertFalse(payload.getBenefits().isEmpty());
        assertFalse(payload.getIngredients().isEmpty());
        assertFalse(payload.getUsageSteps().isEmpty());
    }
}