package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.ProductIngredientRepository;
import learn.DeVitoStyles.data.mappers.ProductIngredientMapper;
import learn.DeVitoStyles.models.Products.ProductIngredient;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductIngredientJdbcClientRepository implements ProductIngredientRepository {

    private final JdbcClient jdbcClient;
    private static final ProductIngredientMapper MAPPER = new ProductIngredientMapper();

    public ProductIngredientJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }


    private static final String SELECT = """
            SELECT
                ingredient_id,
                product_id,
                ingredient,
                display_order
            FROM product_ingredients
            WHERE product_id = :productId
            ORDER BY display_order;
            """;

    @Override
    public List<ProductIngredient> findByProductId(int productId) {

        return jdbcClient.sql(SELECT)
                .param("productId", productId)
                .query(MAPPER)
                .list();
    }
}