package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.Products.ProductIngredient;

import java.util.List;

public interface ProductIngredientRepository {

    List<ProductIngredient> findByProductId(int productId);

}
