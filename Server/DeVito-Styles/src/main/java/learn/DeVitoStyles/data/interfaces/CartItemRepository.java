package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.ShoppingCart.CartItem;

import java.util.List;

public interface CartItemRepository {

    List<CartItem> findByCartId(int cartId);

    CartItem findByCartIdAndProductId(int cartId, int productId);

    CartItem add(CartItem item);

    boolean update(CartItem cartItem);

    boolean deleteById(int cartItemId);

    boolean deleteByCartId(int cartId);

}