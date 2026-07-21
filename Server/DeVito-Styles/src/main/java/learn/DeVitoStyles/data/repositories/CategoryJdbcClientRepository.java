package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.models.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryJdbcClientRepository {

    public List<Category> findAll() {

        return List.of();
    }

    public Category findById(int categoryId) {

        return null;
    }
}
