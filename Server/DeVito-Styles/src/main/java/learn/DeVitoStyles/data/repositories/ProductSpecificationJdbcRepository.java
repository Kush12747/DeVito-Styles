package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.ProductSpecificationRepository;
import learn.DeVitoStyles.data.mappers.ProductSpecificationMapper;
import learn.DeVitoStyles.models.Products.ProductSpecification;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ProductSpecificationJdbcRepository implements ProductSpecificationRepository {

    private final JdbcClient jdbcClient;
    private final ProductSpecificationMapper mapper = new ProductSpecificationMapper();

    private static final String BASE_SELECT = """
            SELECT product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku
            FROM product_specifications WHERE product_id = :productId;
            """;

    public ProductSpecificationJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public ProductSpecification findByProductId(int productId) {

        return jdbcClient.sql(BASE_SELECT)
                .param("productId", productId)
                .query(mapper)
                .optional()
                .orElse(null);
    }


}
