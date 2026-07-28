package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.Products.ProductSpecification;

public interface ProductSpecificationRepository {

    ProductSpecification findByProductId(int productId);
}
