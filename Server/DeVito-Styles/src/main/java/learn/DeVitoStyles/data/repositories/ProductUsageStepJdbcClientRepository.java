package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.ProductUsageStepRepository;
import learn.DeVitoStyles.models.Products.ProductUsageStep;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductUsageStepJdbcClientRepository implements ProductUsageStepRepository {

    private final JdbcClient jdbcClient;

    public ProductUsageStepJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private final RowMapper<ProductUsageStep> mapper = (rs, rowNum) -> {

        ProductUsageStep step = new ProductUsageStep();

        step.setStepId(rs.getInt("step_id"));
        step.setProductId(rs.getInt("product_id"));
        step.setStepNumber(rs.getInt("step_number"));
        step.setInstruction(rs.getString("instruction"));

        return step;
    };

    private static final String SELECT = """
            SELECT
                step_id,
                product_id,
                step_number,
                instruction
            FROM product_usage_steps
            WHERE product_id = :productId
            ORDER BY step_number;
            """;

    @Override
    public List<ProductUsageStep> findByProductId(int productId) {

        return jdbcClient.sql(SELECT)
                .param("productId", productId)
                .query(mapper)
                .list();
    }
}