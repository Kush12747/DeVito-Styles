package learn.DeVitoStyles.data.mappers;

import learn.DeVitoStyles.models.Products.ProductBenefit;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductBenefitMapper implements RowMapper<ProductBenefit> {

    @Override
    public ProductBenefit mapRow(ResultSet rs, int rowNum) throws SQLException {

        ProductBenefit benefit = new ProductBenefit();

        benefit.setBenefitId(rs.getInt("benefit_id"));
        benefit.setProductId(rs.getInt("product_id"));
        benefit.setBenefit(rs.getString("benefit"));
        benefit.setDisplayOrder(rs.getInt("display_order"));

        return benefit;
    }
}
