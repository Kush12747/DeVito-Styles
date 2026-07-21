package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.CategoryService;
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
    public List<Category> findAll() {
        return service.findAll();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> findById(@PathVariable int categoryId) {

        Category category = service.findById(categoryId);

        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(category);
    }
}