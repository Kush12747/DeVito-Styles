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

    public Product findById(int productId) {
        return productRepository.findById(productId);
    }

    public List<Product> find(String keyword, Integer categoryId, Boolean featured, String sort) {
        return productRepository.find(keyword, categoryId, featured, sort);
    }
}
