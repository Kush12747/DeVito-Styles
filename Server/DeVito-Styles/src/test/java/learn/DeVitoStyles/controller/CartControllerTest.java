package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.domain.ShoppingCartService;
import learn.DeVitoStyles.models.Products.Product;
import learn.DeVitoStyles.models.ShoppingCart.Cart;
import learn.DeVitoStyles.models.ShoppingCart.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private ShoppingCartService service;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CartController(service)).build();
    }

    private static Product product(int id) {
        return new Product(id, 1, "Pomade", "Strong hold", new BigDecimal("18.99"), 50,
                "https://img/pomade.png", true, true,
                LocalDateTime.of(2026, 1, 1, 0, 0), LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    private static CartItem item(int cartItemId, int productId, int qty) {
        return new CartItem(cartItemId, 10, product(productId), qty);
    }

    private static <T> Result<T> success(T payload) {
        Result<T> result = new Result<>();
        result.setpayload(payload);
        return result;
    }

    private static <T> Result<T> failure(String message, ResultType type) {
        Result<T> result = new Result<>();
        result.addErrorMessage(message, type);
        return result;
    }

    // ---------- GET /{userId} ----------

    @Test
    void getCartByUserId_whenFound_returns200WithCartAndItems() throws Exception {
        Cart cart = new Cart(10, 3, LocalDateTime.of(2026, 1, 1, 0, 0), LocalDateTime.of(2026, 1, 2, 0, 0));
        cart.setItems(List.of(item(1, 5, 2), item(2, 6, 1)));
        when(service.getCartByUserId(3)).thenReturn(success(cart));

        mvc.perform(get("/api/cart/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(10))
                .andExpect(jsonPath("$.userId").value(3))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].product.productId").value(5))
                .andExpect(jsonPath("$.items[0].product.name").value("Pomade"));

        verify(service).getCartByUserId(3);
    }

    @Test
    void getCartByUserId_whenNotFound_returns404() throws Exception {
        when(service.getCartByUserId(99)).thenReturn(failure("Cart not found", ResultType.NOT_FOUND));

        mvc.perform(get("/api/cart/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Cart not found"));
    }

    @Test
    void getCartByUserId_whenInvalid_returns400() throws Exception {
        when(service.getCartByUserId(0)).thenReturn(failure("Invalid user id", ResultType.INVALID));

        mvc.perform(get("/api/cart/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Invalid user id"));
    }

    // ---------- POST /{userId}/items ----------

    @Test
    void addToCart_whenSuccess_returns200WithCartItem() throws Exception {
        when(service.addToCart(3, 5, 2)).thenReturn(success(item(1, 5, 2)));

        mvc.perform(post("/api/cart/3/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":5,\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItemId").value(1))
                .andExpect(jsonPath("$.cartId").value(10))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.product.productId").value(5));

        verify(service).addToCart(3, 5, 2);
    }

    @Test
    void addToCart_whenInvalid_returns400() throws Exception {
        when(service.addToCart(3, 5, 0)).thenReturn(failure("Quantity must be greater than 0", ResultType.INVALID));

        mvc.perform(post("/api/cart/3/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":5,\"quantity\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Quantity must be greater than 0"));
    }

    @Test
    void addToCart_whenProductNotFound_returns404() throws Exception {
        when(service.addToCart(3, 999, 1)).thenReturn(failure("Product not found", ResultType.NOT_FOUND));

        mvc.perform(post("/api/cart/3/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":999,\"quantity\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Product not found"));
    }

    // ---------- PUT /{userId}/items/{productId} ----------

    @Test
    void updateQuantity_whenSuccess_returns200WithUpdatedItem() throws Exception {
        when(service.updateQuantity(3, 5, 4)).thenReturn(success(item(1, 5, 4)));

        mvc.perform(put("/api/cart/3/items/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(4))
                .andExpect(jsonPath("$.product.productId").value(5));

        verify(service).updateQuantity(3, 5, 4);
    }

    @Test
    void updateQuantity_whenItemNotFound_returns404() throws Exception {
        when(service.updateQuantity(3, 5, 4)).thenReturn(failure("Item not in cart", ResultType.NOT_FOUND));

        mvc.perform(put("/api/cart/3/items/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":4}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Item not in cart"));
    }

    @Test
    void updateQuantity_whenInvalid_returns400() throws Exception {
        when(service.updateQuantity(3, 5, 500)).thenReturn(failure("Not enough stock", ResultType.INVALID));

        mvc.perform(put("/api/cart/3/items/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":500}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Not enough stock"));
    }

    // ---------- DELETE /{userId}/items/{productId} ----------

    @Test
    void removeItemFromCart_whenSuccess_returns204() throws Exception {
        when(service.removeFromCart(3, 5)).thenReturn(new Result<>());

        mvc.perform(delete("/api/cart/3/items/5"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).removeFromCart(3, 5);
    }

    @Test
    void removeItemFromCart_whenNotFound_returns404() throws Exception {
        when(service.removeFromCart(3, 5)).thenReturn(failure("Item not in cart", ResultType.NOT_FOUND));

        mvc.perform(delete("/api/cart/3/items/5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Item not in cart"));
    }

    // ---------- DELETE /{userId} ----------

    @Test
    void clearCart_whenSuccess_returns204() throws Exception {
        when(service.clearCart(3)).thenReturn(new Result<>());

        mvc.perform(delete("/api/cart/3"))
                .andExpect(status().isNoContent());

        verify(service).clearCart(3);
    }

    @Test
    void clearCart_whenNotFound_returns404() throws Exception {
        when(service.clearCart(99)).thenReturn(failure("Cart not found", ResultType.NOT_FOUND));

        mvc.perform(delete("/api/cart/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Cart not found"));
    }

    @Test
    void clearCart_whenInvalid_returns400() throws Exception {
        when(service.clearCart(0)).thenReturn(failure("Invalid user id", ResultType.INVALID));

        mvc.perform(delete("/api/cart/0"))
                .andExpect(status().isBadRequest());
    }
}
