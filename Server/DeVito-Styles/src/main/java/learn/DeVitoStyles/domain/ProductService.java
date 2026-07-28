package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.*;
import learn.DeVitoStyles.models.Products.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductBenefitRepository benefitRepository;
    private final ProductIngredientRepository ingredientRepository;
    private final ProductSpecificationRepository specificationRepository;
    private final ProductUsageStepRepository usageStepRepository;

    public ProductService(ProductRepository productRepository, ProductBenefitRepository benefitRepository, ProductIngredientRepository ingredientRepository, ProductSpecificationRepository specificationRepository, ProductUsageStepRepository usageStepRepository) {
        this.productRepository = productRepository;
        this.benefitRepository = benefitRepository;
        this.ingredientRepository = ingredientRepository;
        this.specificationRepository = specificationRepository;
        this.usageStepRepository = usageStepRepository;
    }


    public Result<Product> findById(int productId) {

        Result<Product> result = new Result<>();

        Product product = productRepository.findById(productId);

        if (product == null) {
            result.addErrorMessage("Product with id %s was not found.", ResultType.NOT_FOUND, productId);
            return result;
        }

        product.setSpecification(specificationRepository.findByProductId(productId));
        product.setBenefits(benefitRepository.findByProductId(productId));
        product.setIngredients(ingredientRepository.findByProductId(productId));
        product.setUsageSteps(usageStepRepository.findByProductId(productId));

        result.setpayload(product);

        return result;
    }


    public Result<List<Product>> find(
            String keyword,
            Integer categoryId,
            Boolean featured,
            String sort
    ) {

        Result<List<Product>> result = new Result<>();

        List<Product> products = productRepository.find(
                keyword,
                categoryId,
                featured,
                sort
        );

        result.setpayload(products);

        return result;
    }

    public List<Product> findRelatedProducts(int productId) {
        return productRepository.findRelatedProducts(productId);
    }
}