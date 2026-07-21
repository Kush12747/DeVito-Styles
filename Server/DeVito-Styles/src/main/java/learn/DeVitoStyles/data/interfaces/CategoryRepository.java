package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.Category;

import java.util.List;

public interface CategoryRepository {

    List<Category> findAll();

    Category findById(int categoryId);
}
