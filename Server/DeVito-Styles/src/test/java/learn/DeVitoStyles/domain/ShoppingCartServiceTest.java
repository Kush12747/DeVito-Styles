package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.CartItemRepository;
import learn.DeVitoStyles.data.interfaces.CartRepository;
import learn.DeVitoStyles.data.interfaces.ProductRepository;
import learn.DeVitoStyles.models.Products.Product;
import learn.DeVitoStyles.models.ShoppingCart.Cart;
import learn.DeVitoStyles.models.ShoppingCart.CartItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ShoppingCartServiceTest {

    @Autowired
    ShoppingCartService service;

    @MockBean
    CartRepository cartRepository;

    @MockBean
    CartItemRepository cartItemRepository;

    @MockBean
    ProductRepository productRepository;


    private Cart makeCart(int cartId) {

        Cart cart = new Cart();

        cart.setCartId(cartId);
        cart.setUserId(1);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        return cart;
    }


    private Product makeProduct(int productId) {

        Product product = new Product();

        product.setProductId(productId);
        product.setName("Hair Clay");
        product.setPrice(BigDecimal.valueOf(20));
        product.setStockQuantity(10);
        product.setActive(true);

        return product;
    }


    private CartItem makeCartItem(int cartItemId) {

        CartItem item = new CartItem();

        item.setCartItemId(cartItemId);
        item.setCartId(1);
        item.setProduct(makeProduct(1));
        item.setQuantity(2);

        return item;
    }


    @Test
    void shouldGetCartByUserId() {

        Cart cart = makeCart(1);

        when(cartRepository.findByUserId(1))
                .thenReturn(cart);

        when(cartItemRepository.findByCartId(1))
                .thenReturn(java.util.List.of());


        Result<Cart> result =
                service.getCartByUserId(1);


        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getCartId());
    }


    @Test
    void shouldNotGetCartWithInvalidUserId() {

        Result<Cart> result =
                service.getCartByUserId(0);


        assertFalse(result.isSuccess());
        assertEquals(
                ResultType.INVALID,
                result.getResultType()
        );
    }


    @Test
    void shouldAddNewItemToCart() {

        Cart cart = makeCart(1);
        Product product = makeProduct(1);

        CartItem addedItem = makeCartItem(5);


        when(cartRepository.findByUserId(1))
                .thenReturn(cart);

        when(productRepository.findById(1))
                .thenReturn(product);

        when(cartItemRepository.findByCartIdAndProductId(1,1))
                .thenReturn(null);

        when(cartItemRepository.add(org.mockito.ArgumentMatchers.any()))
                .thenReturn(addedItem);



        Result<CartItem> result =
                service.addToCart(1,1,2);


        assertTrue(result.isSuccess());
        assertEquals(5,
                result.getpayload().getCartItemId());
    }


    @Test
    void shouldNotAddWhenProductDoesNotExist() {


        when(productRepository.findById(99))
                .thenReturn(null);


        Result<CartItem> result =
                service.addToCart(1,99,2);


        assertFalse(result.isSuccess());
        assertEquals(
                ResultType.INVALID,
                result.getResultType()
        );
    }



    @Test
    void shouldNotAddWhenQuantityExceedsStock() {

        Product product = makeProduct(1);

        product.setStockQuantity(2);


        when(productRepository.findById(1))
                .thenReturn(product);


        Result<CartItem> result =
                service.addToCart(1,1,5);



        assertFalse(result.isSuccess());
        assertEquals(
                ResultType.INVALID,
                result.getResultType()
        );
    }



    @Test
    void shouldUpdateExistingCartItem() {


        Cart cart = makeCart(1);

        Product product = makeProduct(1);

        CartItem item = makeCartItem(1);


        when(cartRepository.findByUserId(1))
                .thenReturn(cart);

        when(productRepository.findById(1))
                .thenReturn(product);

        when(cartItemRepository.findByCartIdAndProductId(1,1))
                .thenReturn(item);

        when(cartItemRepository.update(item))
                .thenReturn(true);



        Result<CartItem> result =
                service.updateQuantity(1,1,5);



        assertTrue(result.isSuccess());
        assertEquals(
                5,
                result.getpayload().getQuantity()
        );
    }



    @Test
    void shouldNotUpdateInvalidQuantity() {


        Result<CartItem> result =
                service.updateQuantity(1,1,0);


        assertFalse(result.isSuccess());

        assertEquals(
                ResultType.INVALID,
                result.getResultType()
        );
    }



    @Test
    void shouldRemoveCartItem() {


        Cart cart = makeCart(1);

        CartItem item = makeCartItem(5);

        when(cartRepository.findByUserId(1))
                .thenReturn(cart);

        when(cartItemRepository.findByCartIdAndProductId(1,1))
                .thenReturn(item);


        when(cartItemRepository.deleteById(5))
                .thenReturn(true);

        Result<Void> result =
                service.removeFromCart(1,1);

        assertTrue(result.isSuccess());
    }



    @Test
    void shouldClearCart() {


        Cart cart = makeCart(1);


        when(cartRepository.findByUserId(1))
                .thenReturn(cart);


        when(cartItemRepository.deleteByCartId(1))
                .thenReturn(true);



        Result<Void> result =
                service.clearCart(1);



        assertTrue(result.isSuccess());
    }



    @Test
    void shouldNotClearCartWithInvalidUserId() {


        Result<Void> result =
                service.clearCart(0);



        assertFalse(result.isSuccess());

        assertEquals(
                ResultType.INVALID,
                result.getResultType()
        );
    }
}