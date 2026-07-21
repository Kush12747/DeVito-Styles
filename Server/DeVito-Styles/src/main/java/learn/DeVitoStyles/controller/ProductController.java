package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.ProductService;
import learn.DeVitoStyles.models.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/products")
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> findById(@PathVariable int productId) {
        Product product = productService.findById(productId);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product);
    }

    @GetMapping
    public List<Product> find(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false, defaultValue = "name") String sort) {
        return productService.find(keyword, categoryId, featured, sort);
    }
}
