package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.*;
import learn.DeVitoStyles.models.Products.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductBenefitRepository benefitRepository;
    @Mock private ProductIngredientRepository ingredientRepository;
    @Mock private ProductSpecificationRepository specificationRepository;
    @Mock private ProductUsageStepRepository usageStepRepository;

    @InjectMocks private ProductService service;

    @Test
    void shouldFindByIdWithDetails() {
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

        when(productRepository.findById(1)).thenReturn(product);
        when(specificationRepository.findByProductId(1)).thenReturn(specification);
        when(benefitRepository.findByProductId(1)).thenReturn(List.of(benefit));
        when(ingredientRepository.findByProductId(1)).thenReturn(List.of(ingredient));
        when(usageStepRepository.findByProductId(1)).thenReturn(List.of(step));

        Result<Product> result = service.findById(1);

        assertTrue(result.isSuccess());

        Product payload = result.getpayload();
        assertEquals(1, payload.getProductId());
        assertEquals("Hair Gel", payload.getName());
        assertEquals("4 oz", payload.getSpecification().getSize());
        assertEquals(List.of(benefit), payload.getBenefits());
        assertEquals(List.of(ingredient), payload.getIngredients());
        assertEquals(List.of(step), payload.getUsageSteps());
    }

    @Test
    void shouldNotFindById() {
        when(productRepository.findById(999)).thenReturn(null);

        Result<Product> result = service.findById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Product with id 999 was not found."), result.getMessages());
        assertNull(result.getpayload());

        verifyNoInteractions(specificationRepository, benefitRepository,
                ingredientRepository, usageStepRepository);
    }

    @Test
    void shouldFindProducts() {
        Product product = new Product();
        product.setProductId(1);
        product.setName("Hair Gel");

        when(productRepository.find("gel", null, null, "priceAsc")).thenReturn(List.of(product));

        Result<List<Product>> result = service.find("gel", null, null, "priceAsc");

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
        assertEquals("Hair Gel", result.getpayload().get(0).getName());
    }

    @Test
    void shouldFindFeaturedProductsByCategory() {
        Product product = new Product();
        product.setProductId(1);
        product.setName("Premium Shampoo");

        when(productRepository.find(null, 2, true, "priceDesc")).thenReturn(List.of(product));

        Result<List<Product>> result = service.find(null, 2, true, "priceDesc");

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
        assertEquals("Premium Shampoo", result.getpayload().get(0).getName());
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsMatch() {
        when(productRepository.find("unknown", null, null, "name")).thenReturn(List.of());

        Result<List<Product>> result = service.find("unknown", null, null, "name");

        assertTrue(result.isSuccess());
        assertTrue(result.getpayload().isEmpty());
    }

    @Test
    void shouldFindRelatedProducts() {
        Product related = new Product();
        related.setProductId(2);

        when(productRepository.findRelatedProducts(1)).thenReturn(List.of(related));

        List<Product> result = service.findRelatedProducts(1);

        assertEquals(List.of(related), result);
        verify(productRepository, never()).findById(anyInt());
    }
}
