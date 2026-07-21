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

    public Result<List<Category>> findAll() {

        Result<List<Category>> result = new Result<>();

        List<Category> categories = repository.findAll();

        result.setpayload(categories);

        return result;
    }


    public Result<Category> findById(int categoryId) {

        Result<Category> result = new Result<>();

        Category category = repository.findById(categoryId);

        if (category == null) {
            result.addErrorMessage(
                    "Category with id %s was not found.",
                    ResultType.NOT_FOUND,
                    categoryId
            );
            return result;
        }

        result.setpayload(category);

        return result;
    }
}