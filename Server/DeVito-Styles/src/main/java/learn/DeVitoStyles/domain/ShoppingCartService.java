package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.CartItemRepository;
import learn.DeVitoStyles.data.interfaces.CartRepository;
import learn.DeVitoStyles.data.interfaces.ProductRepository;
import learn.DeVitoStyles.models.Products.Product;
import learn.DeVitoStyles.models.ShoppingCart.Cart;
import learn.DeVitoStyles.models.ShoppingCart.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public ShoppingCartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Result<Cart> getCartByUserId(int userId) {
        Result<Cart> result = new Result<>();

        if (userId <= 0) {
            result.addErrorMessage("user id must be greater than 0", ResultType.INVALID);
            return result;
        }

        Cart cart = getOrCreateCart(userId);

        List<CartItem> items = cartItemRepository.findByCartId(cart.getCartId());

        cart.setItems(items);

        result.setpayload(cart);

        return result;
    }

    public Result<CartItem> addToCart(int userId, int productId, int quantity) {
        Result<CartItem> result = new Result<>();

        if (isInvalidInput(userId, productId, quantity, result)) {
            return result;
        }

        Product product = validateProduct(productId, quantity, result);

        if (!result.isSuccess()) {
            return result;
        }

        // get or create the cart for the user
        Cart cart = getOrCreateCart(userId);

        if (cart == null) {
            result.addErrorMessage("unable to create chopping cart", ResultType.INVALID);
            return result;
        }

        // Find product
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId);

        if (existingItem != null) {

            return updateExistingItem(existingItem, quantity, product);
        }

        return createCartItem(cart, product, quantity);
    }

    public Result<CartItem> updateQuantity(int userId, int productId, int quantity) {

        return null;
    }

    public Result<Void> removeFromCart(int userId, int productId) {
        Result<Void> result = new Result<>();

        if (userId <= 0 || productId <= 0) {
            result.addErrorMessage("Invalid ids", ResultType.INVALID);
            return result;
        }
        
        boolean item = cartItemRepository.deleteById(productId);
        
        if (!item) {
            result.addErrorMessage("Item count not be removed", ResultType.NOT_FOUND);
            return result;
        }
        
        return result;
    }

    public Result<Void> clearCart(int userId) {
        Result<Void> result = new Result<>();

        if (userId <= 0) {
            result.addErrorMessage("Invalid ids", ResultType.INVALID);
            return result;
        }

        // Get the users cart
        Cart cart = getOrCreateCart(userId);

        if (cart == null) {
            result.addErrorMessage("No cart was found", ResultType.INVALID);
            return result;
        }

        boolean deleteFromCart = cartItemRepository.deleteByCartId(cart.getCartId());

        if (!deleteFromCart) {
            result.addErrorMessage("Can't delete from cart", ResultType.NOT_FOUND);
            return result;
        }

        return result;
    }

    private boolean isInvalidInput(int userId, int productId, int quantity, Result<?> result) {
        // validate the id's
        if (userId <= 0 || productId <= 0 || quantity <= 0) {
            result.addErrorMessage("Invalid inputs", ResultType.INVALID);
            return true;
        }
        return false;
    }

    private Product validateProduct(int productId, int quantity, Result<?> result) {
        Product product = productRepository.findById(productId);

        if (product == null) {
            result.addErrorMessage("product does not exist.", ResultType.INVALID);
            return null;
        }

        if (!product.isActive()) {
            result.addErrorMessage("Product is inactive", ResultType.INVALID);
            return null;
        }

        int stockQuantity = product.getStockQuantity();

        // If the requested stock is greater than backend amount
        if (quantity > stockQuantity) {
            result.addErrorMessage("Not enough stock available.", ResultType.INVALID);
            return null;
        }

        return product;
    }

    private Result<CartItem> updateExistingItem(CartItem existingItem, int quantity, Product product) {
        Result<CartItem> result = new Result<>();

        int newQuantity = existingItem.getQuantity() + quantity;
        int stockQuantity = product.getStockQuantity();

        if (newQuantity > stockQuantity) {
            result.addErrorMessage("Quantity exceeds available stock", ResultType.INVALID);
            return result;
        }

        existingItem.setQuantity(newQuantity);

        // if product exists then update cart
        if (!cartItemRepository.update(existingItem)) {
            result.addErrorMessage("Unable to update cart item", ResultType.INVALID);
            return result;
        }

        result.setpayload(existingItem);
        return result;
    }

    private Result<CartItem> createCartItem(Cart cart, Product product, int quantity) {
        Result<CartItem> result = new Result<>();

        // Product is not already in the cart
        CartItem newItem = new CartItem();

        newItem.setCartId(cart.getCartId());
        newItem.setProduct(product);
        newItem.setQuantity(quantity);

        newItem = cartItemRepository.add(newItem);

        if (newItem == null) {
            result.addErrorMessage("Unable to add item to cart.", ResultType.INVALID);
            return result;
        }

        result.setpayload(newItem);
        return result;
    }

    private Cart getOrCreateCart(int userId) {

        Cart cart = cartRepository.findByUserId(userId);

        if (cart != null) {
            return cart;
        }

        cart = new Cart();
        cart.setUserId(userId);

        return cartRepository.add(cart);
    }
}
