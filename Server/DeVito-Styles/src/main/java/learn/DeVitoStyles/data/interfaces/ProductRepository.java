package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.Products.Product;

import java.util.List;

public interface ProductRepository {


    Product findById(int productId);

    List<Product> find(String keyword, Integer categoryId, Boolean featured, String sort);

    List<Product> findRelatedProducts(int productId);
}
