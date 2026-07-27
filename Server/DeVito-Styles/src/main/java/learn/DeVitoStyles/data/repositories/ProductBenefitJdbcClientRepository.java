package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.ProductBenefitRepository;
import learn.DeVitoStyles.data.mappers.ProductBenefitMapper;
import learn.DeVitoStyles.models.Products.ProductBenefit;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductBenefitJdbcClientRepository implements ProductBenefitRepository {

    private final JdbcClient jdbcClient;
    private final ProductBenefitMapper mapper = new ProductBenefitMapper();

    private static final String BASE_SELECT = """
            SELECT
                benefit_id,
                product_id,
                benefit,
                display_order
            FROM product_benefits
            WHERE product_id = :productId
            ORDER BY display_order;
            """;

    public ProductBenefitJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<ProductBenefit> findByProductId(int productId) {
        return jdbcClient.sql(BASE_SELECT)
                .param("productId", productId)
                .query(mapper)
                .list();
    }
}
