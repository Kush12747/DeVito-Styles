package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.CategoryRepository;
import learn.DeVitoStyles.models.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Category findById(int categoryId) {
        return repository.findById(categoryId);
    }
}