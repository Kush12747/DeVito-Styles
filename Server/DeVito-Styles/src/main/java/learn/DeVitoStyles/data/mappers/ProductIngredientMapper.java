package learn.DeVitoStyles.data.mappers;

import learn.DeVitoStyles.models.Products.ProductIngredient;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductIngredientMapper implements RowMapper<ProductIngredient> {

    @Override
    public ProductIngredient mapRow(ResultSet rs, int rowNum) throws SQLException {

        ProductIngredient ingredient = new ProductIngredient();

        ingredient.setIngredientId(rs.getInt("ingredient_id"));
        ingredient.setProductId(rs.getInt("product_id"));
        ingredient.setIngredient(rs.getString("ingredient"));
        ingredient.setDisplayOrder(rs.getInt("display_order"));

        return ingredient;
    }
}