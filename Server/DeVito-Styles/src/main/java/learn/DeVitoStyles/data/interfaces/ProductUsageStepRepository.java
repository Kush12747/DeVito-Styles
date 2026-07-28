package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.Products.ProductUsageStep;

import java.util.List;

public interface ProductUsageStepRepository {

    List<ProductUsageStep> findByProductId(int productId);

}