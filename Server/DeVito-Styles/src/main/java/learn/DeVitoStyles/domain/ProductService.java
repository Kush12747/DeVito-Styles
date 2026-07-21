package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.ProductRepository;
import learn.DeVitoStyles.models.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public Result<Product> findById(int productId) {

        Result<Product> result = new Result<>();

        Product product = productRepository.findById(productId);

        if (product == null) {
            result.addErrorMessage(
                    "Product with id %s was not found.",
                    ResultType.NOT_FOUND,
                    productId
            );
            return result;
        }

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
}