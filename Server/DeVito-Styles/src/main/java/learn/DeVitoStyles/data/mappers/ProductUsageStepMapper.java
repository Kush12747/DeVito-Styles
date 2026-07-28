package learn.DeVitoStyles.data.mappers;

import learn.DeVitoStyles.models.Products.ProductUsageStep;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductUsageStepMapper implements RowMapper<ProductUsageStep> {

    @Override
    public ProductUsageStep mapRow(ResultSet rs, int rowNum) throws SQLException {

        ProductUsageStep step = new ProductUsageStep();

        step.setStepId(rs.getInt("step_id"));
        step.setProductId(rs.getInt("product_id"));
        step.setStepNumber(rs.getInt("step_number"));
        step.setInstruction(rs.getString("instruction"));

        return step;
    }
}