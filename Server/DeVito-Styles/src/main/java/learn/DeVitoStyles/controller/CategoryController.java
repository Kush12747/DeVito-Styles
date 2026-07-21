package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.CategoryService;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.models.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<Category>> findAll() {

        Result<List<Category>> result = service.findAll();

        if (!result.isSuccess()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result.getpayload());
    }


    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> findById(@PathVariable int categoryId) {

        Result<Category> result = service.findById(categoryId);

        if (!result.isSuccess()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result.getpayload());
    }
}