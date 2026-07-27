package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.Products.ProductBenefit;

import java.util.List;

public interface ProductBenefitRepository {

    List<ProductBenefit> findByProductId(int productId);
}
