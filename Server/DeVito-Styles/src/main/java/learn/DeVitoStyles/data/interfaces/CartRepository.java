package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.ShoppingCart.Cart;

public interface CartRepository {

    Cart findByUserId(int userId);

    Cart findById(int cartId);

    Cart add(Cart cart);

    boolean deleteById(int cartId);
}
