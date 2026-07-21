package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.ProductService;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.models.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/{productId}")
    public ResponseEntity<Product> findById(@PathVariable int productId) {

        Result<Product> result = productService.findById(productId);

        if (!result.isSuccess()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result.getpayload());
    }


    @GetMapping
    public ResponseEntity<List<Product>> find(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false, defaultValue = "name") String sort) {

        Result<List<Product>> result = productService.find(
                keyword,
                categoryId,
                featured,
                sort
        );

        return ResponseEntity.ok(result.getpayload());
    }
}