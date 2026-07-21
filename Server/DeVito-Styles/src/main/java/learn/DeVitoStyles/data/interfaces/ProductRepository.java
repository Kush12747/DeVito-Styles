package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.Product;

import java.util.List;

public interface ProductRepository {


    Product findById(int productId);

    List<Product> find(String keyword, Integer categoryId, Boolean featured, String sort);
}
