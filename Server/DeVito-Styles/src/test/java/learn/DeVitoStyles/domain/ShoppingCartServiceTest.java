package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.CartItemRepository;
import learn.DeVitoStyles.data.interfaces.CartRepository;
import learn.DeVitoStyles.data.interfaces.ProductRepository;
import learn.DeVitoStyles.models.Products.Product;
import learn.DeVitoStyles.models.ShoppingCart.Cart;
import learn.DeVitoStyles.models.ShoppingCart.CartItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks private ShoppingCartService service;

    // =========================
    // GET CART
    // =========================

    @Test
    void shouldGetCartByUserId() {
        CartItem item = makeCartItem(5, 2);
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartId(1)).thenReturn(List.of(item));

        Result<Cart> result = service.getCartByUserId(1);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getCartId());
        assertEquals(List.of(item), result.getpayload().getItems());
        verify(cartRepository, never()).add(any());
    }

    @Test
    void shouldCreateCartWhenUserHasNone() {
        when(cartRepository.findByUserId(1)).thenReturn(null);
        when(cartRepository.add(any(Cart.class))).thenReturn(makeCart(7));
        when(cartItemRepository.findByCartId(7)).thenReturn(List.of());

        Result<Cart> result = service.getCartByUserId(1);

        assertTrue(result.isSuccess());
        assertEquals(7, result.getpayload().getCartId());
        assertTrue(result.getpayload().getItems().isEmpty());

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).add(captor.capture());
        assertEquals(1, captor.getValue().getUserId());
    }

    @Test
    void shouldNotGetCartWithInvalidUserId() {
        Result<Cart> result = service.getCartByUserId(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("user id must be greater than 0"), result.getMessages());
        verifyNoInteractions(cartRepository, cartItemRepository);
    }

    // =========================
    // ADD TO CART
    // =========================

    @Test
    void shouldAddNewItemToCart() {
        Product product = makeProduct(1, 10);
        CartItem addedItem = makeCartItem(5, 2);

        when(productRepository.findById(1)).thenReturn(product);
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(null);
        when(cartItemRepository.add(any(CartItem.class))).thenReturn(addedItem);

        Result<CartItem> result = service.addToCart(1, 1, 2);

        assertTrue(result.isSuccess());
        assertEquals(5, result.getpayload().getCartItemId());

        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).add(captor.capture());
        assertEquals(1, captor.getValue().getCartId());
        assertSame(product, captor.getValue().getProduct());
        assertEquals(2, captor.getValue().getQuantity());
        verify(cartItemRepository, never()).update(any());
    }

    @Test
    void shouldIncreaseQuantityWhenItemAlreadyInCart() {
        CartItem existing = makeCartItem(5, 3);

        when(productRepository.findById(1)).thenReturn(makeProduct(1, 10));
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(existing);
        when(cartItemRepository.update(existing)).thenReturn(true);

        Result<CartItem> result = service.addToCart(1, 1, 2);

        assertTrue(result.isSuccess());
        assertEquals(5, result.getpayload().getQuantity());
        verify(cartItemRepository, never()).add(any());
    }

    @Test
    void shouldNotAddWithInvalidInput() {
        Result<CartItem> result = service.addToCart(1, 1, 0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Invalid inputs"), result.getMessages());
        verifyNoInteractions(productRepository, cartRepository, cartItemRepository);
    }

    @Test
    void shouldNotAddWhenProductDoesNotExist() {
        when(productRepository.findById(99)).thenReturn(null);

        Result<CartItem> result = service.addToCart(1, 99, 2);

        assertAddRejected(result, "product does not exist.");
    }

    @Test
    void shouldNotAddInactiveProduct() {
        Product product = makeProduct(1, 10);
        product.setActive(false);
        when(productRepository.findById(1)).thenReturn(product);

        Result<CartItem> result = service.addToCart(1, 1, 2);

        assertAddRejected(result, "Product is inactive");
    }

    @Test
    void shouldNotAddWhenQuantityExceedsStock() {
        when(productRepository.findById(1)).thenReturn(makeProduct(1, 2));

        Result<CartItem> result = service.addToCart(1, 1, 5);

        assertAddRejected(result, "Not enough stock available.");
    }

    @Test
    void shouldNotAddWhenCartCannotBeCreated() {
        when(productRepository.findById(1)).thenReturn(makeProduct(1, 10));
        when(cartRepository.findByUserId(1)).thenReturn(null);
        when(cartRepository.add(any(Cart.class))).thenReturn(null);

        Result<CartItem> result = service.addToCart(1, 1, 2);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("unable to create chopping cart"), result.getMessages());
        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void shouldNotAddWhenCombinedQuantityExceedsStock() {
        CartItem existing = makeCartItem(5, 8);

        when(productRepository.findById(1)).thenReturn(makeProduct(1, 10));
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(existing);

        Result<CartItem> result = service.addToCart(1, 1, 3);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Quantity exceeds available stock"), result.getMessages());
        assertEquals(8, existing.getQuantity());
        verify(cartItemRepository, never()).update(any());
    }

    @Test
    void shouldNotAddWhenExistingItemUpdateFails() {
        CartItem existing = makeCartItem(5, 3);

        when(productRepository.findById(1)).thenReturn(makeProduct(1, 10));
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(existing);
        when(cartItemRepository.update(existing)).thenReturn(false);

        Result<CartItem> result = service.addToCart(1, 1, 2);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Unable to update cart item"), result.getMessages());
        assertNull(result.getpayload());
    }

    @Test
    void shouldNotAddWhenRepositoryAddFails() {
        when(productRepository.findById(1)).thenReturn(makeProduct(1, 10));
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(null);
        when(cartItemRepository.add(any(CartItem.class))).thenReturn(null);

        Result<CartItem> result = service.addToCart(1, 1, 2);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Unable to add item to cart."), result.getMessages());
        assertNull(result.getpayload());
    }

    // =========================
    // UPDATE QUANTITY
    // =========================

    @Test
    void shouldUpdateExistingCartItem() {
        CartItem item = makeCartItem(1, 2);

        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(item);
        when(productRepository.findById(1)).thenReturn(makeProduct(1, 10));
        when(cartItemRepository.update(item)).thenReturn(true);

        Result<CartItem> result = service.updateQuantity(1, 1, 5);

        assertTrue(result.isSuccess());
        assertEquals(5, result.getpayload().getQuantity());
        verify(cartItemRepository).update(item);
    }

    @Test
    void shouldNotUpdateInvalidQuantity() {
        Result<CartItem> result = service.updateQuantity(1, 1, 0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Invalid input"), result.getMessages());
        verifyNoInteractions(cartRepository, cartItemRepository, productRepository);
    }

    @Test
    void shouldNotUpdateWhenCartNotFound() {
        when(cartRepository.findByUserId(1)).thenReturn(null);

        Result<CartItem> result = service.updateQuantity(1, 1, 5);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        verify(cartRepository, never()).add(any());
        verifyNoInteractions(cartItemRepository, productRepository);
    }

    @Test
    void shouldNotUpdateWhenItemNotInCart() {
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(null);

        Result<CartItem> result = service.updateQuantity(1, 1, 5);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Item not found in cart."), result.getMessages());
        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldNotUpdateWhenProductNotFound() {
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(makeCartItem(1, 2));
        when(productRepository.findById(1)).thenReturn(null);

        Result<CartItem> result = service.updateQuantity(1, 1, 5);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("product not found"), result.getMessages());
        verify(cartItemRepository, never()).update(any());
    }

    @Test
    void shouldNotUpdateInactiveProduct() {
        Product product = makeProduct(1, 10);
        product.setActive(false);

        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(makeCartItem(1, 2));
        when(productRepository.findById(1)).thenReturn(product);

        Result<CartItem> result = service.updateQuantity(1, 1, 5);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Product is inactive"), result.getMessages());
        verify(cartItemRepository, never()).update(any());
    }

    @Test
    void shouldNotUpdateWhenQuantityExceedsStock() {
        CartItem item = makeCartItem(1, 2);

        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(item);
        when(productRepository.findById(1)).thenReturn(makeProduct(1, 3));

        Result<CartItem> result = service.updateQuantity(1, 1, 5);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Quantity exceeds available stock"), result.getMessages());
        assertEquals(2, item.getQuantity());
        verify(cartItemRepository, never()).update(any());
    }

    @Test
    void shouldNotUpdateWhenRepositoryUpdateFails() {
        CartItem item = makeCartItem(1, 2);

        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(item);
        when(productRepository.findById(1)).thenReturn(makeProduct(1, 10));
        when(cartItemRepository.update(item)).thenReturn(false);

        Result<CartItem> result = service.updateQuantity(1, 1, 5);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Unable to update cart item."), result.getMessages());
        assertNull(result.getpayload());
    }

    // =========================
    // REMOVE FROM CART
    // =========================

    @Test
    void shouldRemoveCartItem() {
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(makeCartItem(5, 2));
        when(cartItemRepository.deleteById(5)).thenReturn(true);

        Result<Void> result = service.removeFromCart(1, 1);

        assertTrue(result.isSuccess());
        verify(cartItemRepository).deleteById(5);
    }

    @Test
    void shouldNotRemoveWithInvalidIds() {
        Result<Void> result = service.removeFromCart(0, 1);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Invalid ids"), result.getMessages());
        verifyNoInteractions(cartRepository, cartItemRepository);
    }

    @Test
    void shouldNotRemoveWhenCartNotFound() {
        when(cartRepository.findByUserId(1)).thenReturn(null);

        Result<Void> result = service.removeFromCart(1, 1);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Cart not found"), result.getMessages());
        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void shouldNotRemoveWhenItemNotFound() {
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(null);

        Result<Void> result = service.removeFromCart(1, 1);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Item not found"), result.getMessages());
        verify(cartItemRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldNotRemoveWhenRepositoryDeleteFails() {
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(makeCartItem(5, 2));
        when(cartItemRepository.deleteById(5)).thenReturn(false);

        Result<Void> result = service.removeFromCart(1, 1);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Unable to remove item"), result.getMessages());
    }

    // =========================
    // CLEAR CART
    // =========================

    @Test
    void shouldClearCart() {
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.deleteByCartId(1)).thenReturn(true);

        Result<Void> result = service.clearCart(1);

        assertTrue(result.isSuccess());
        verify(cartItemRepository).deleteByCartId(1);
    }

    @Test
    void shouldNotClearCartWithInvalidUserId() {
        Result<Void> result = service.clearCart(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        verifyNoInteractions(cartRepository, cartItemRepository);
    }

    @Test
    void shouldNotClearCartWhenCartCannotBeCreated() {
        when(cartRepository.findByUserId(1)).thenReturn(null);
        when(cartRepository.add(any(Cart.class))).thenReturn(null);

        Result<Void> result = service.clearCart(1);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("No cart was found"), result.getMessages());
        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void shouldNotClearCartWhenRepositoryDeleteFails() {
        when(cartRepository.findByUserId(1)).thenReturn(makeCart(1));
        when(cartItemRepository.deleteByCartId(1)).thenReturn(false);

        Result<Void> result = service.clearCart(1);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Can't delete from cart"), result.getMessages());
    }

    // =========================
    // HELPERS
    // =========================

    private void assertAddRejected(Result<CartItem> result, String expectedMessage) {
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of(expectedMessage), result.getMessages());
        verifyNoInteractions(cartRepository, cartItemRepository);
    }

    private Cart makeCart(int cartId) {
        Cart cart = new Cart();
        cart.setCartId(cartId);
        cart.setUserId(1);
        return cart;
    }

    private Product makeProduct(int productId, int stock) {
        Product product = new Product();
        product.setProductId(productId);
        product.setName("Hair Clay");
        product.setPrice(BigDecimal.valueOf(20));
        product.setStockQuantity(stock);
        product.setActive(true);
        return product;
    }

    private CartItem makeCartItem(int cartItemId, int quantity) {
        CartItem item = new CartItem();
        item.setCartItemId(cartItemId);
        item.setCartId(1);
        item.setProduct(makeProduct(1, 10));
        item.setQuantity(quantity);
        return item;
    }
}
