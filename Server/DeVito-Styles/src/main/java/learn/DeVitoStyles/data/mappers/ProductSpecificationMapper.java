package learn.DeVitoStyles.data.mappers;

import learn.DeVitoStyles.models.Products.ProductSpecification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductSpecificationMapper implements RowMapper<ProductSpecification> {

    @Override
    public ProductSpecification mapRow(ResultSet rs, int rowNum) throws SQLException {

        ProductSpecification specification = new ProductSpecification();

        specification.setProductId(rs.getInt("product_id"));
        specification.setSize(rs.getString("size"));
        specification.setScent(rs.getString("scent"));
        specification.setHairType(rs.getString("hair_type"));
        specification.setHoldStrength(rs.getString("hold_strength"));
        specification.setFinish(rs.getString("finish"));
        specification.setCountryOfOrigin(rs.getString("country_of_origin"));
        specification.setWeight(rs.getString("weight"));
        specification.setSku(rs.getString("sku"));

        return specification;
    }
}
