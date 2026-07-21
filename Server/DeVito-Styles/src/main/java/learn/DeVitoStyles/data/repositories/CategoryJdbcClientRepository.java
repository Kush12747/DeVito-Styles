package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.CategoryRepository;
import learn.DeVitoStyles.data.mappers.CategoryMapper;
import learn.DeVitoStyles.models.Category;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryJdbcClientRepository implements CategoryRepository {

    private final JdbcClient jdbcClient;
    private final CategoryMapper mapper = new CategoryMapper();

    private static final String BASE_SELECT = """
            SELECT
                category_id,
                name,
                description
            FROM categories
            """;

    public CategoryJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Category> findAll() {

        return jdbcClient.sql(BASE_SELECT)
                .query(mapper)
                .list();
    }

    @Override
    public Category findById(int categoryId) {

        final String sql = BASE_SELECT + " WHERE category_id = ?;";

        return jdbcClient.sql(sql)
                .param(categoryId)
                .query(mapper)
                .optional()
                .orElse(null);
    }
}