package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.ProductRepository;
import learn.DeVitoStyles.data.mappers.ProductsMapper;
import learn.DeVitoStyles.models.Product;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductJdbcClientRepository implements ProductRepository {
    private final JdbcClient jdbcClient;
    private final ProductsMapper mapper = new ProductsMapper();

    private static final String BASE_SELECT = """
            SELECT product_id, category_id, name, description, price, stock_quantity,
            image_url, is_featured, is_active, created_at, updated_at
            FROM products
            """;

    public ProductJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Product findById(int productId) {
        final String sql = BASE_SELECT + " WHERE product_id = ?;";

        return jdbcClient.sql(sql)
                .param(productId)
                .query(mapper)
                .optional()
                .orElse(null);
    }

    public List<Product> find(String keyword, Integer categoryId, Boolean featured, String sort) {

        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append(" WHERE is_active = true");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND LOWER(name) LIKE LOWER(?)");
            params.add("%" + keyword + "%");
        }

        if (categoryId != null) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }

        if (Boolean.TRUE.equals(featured)) {
            sql.append(" AND is_featured = true");
        }

        if ("priceAsc".equals(sort)) {
            sql.append(" ORDER BY price ASC");
        } else if ("priceDesc".equals(sort)) {
            sql.append(" ORDER BY price DESC");
        } else {
            sql.append(" ORDER BY name ASC");
        }

        JdbcClient.StatementSpec statement = jdbcClient.sql(sql.toString());

        for (int i = 0; i < params.size(); i++) {
            statement = statement.param(i + 1, params.get(i));
        }

        return statement
                .query(mapper)
                .list();
    }
}