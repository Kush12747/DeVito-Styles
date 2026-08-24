package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ShoppingCartService;
import learn.DeVitoStyles.dto.AddCartItemRequest;
import learn.DeVitoStyles.dto.UpdateCartItemRequest;
import learn.DeVitoStyles.models.ShoppingCart.Cart;
import learn.DeVitoStyles.models.ShoppingCart.CartItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cart")
@CrossOrigin
public class CartController {

    private final ShoppingCartService service;

    public CartController(ShoppingCartService service) {
        this.service = service;
    }


    // GET USER CART
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getCartByUserId(
            @PathVariable int userId) {

        Result<Cart> result = service.getCartByUserId(userId);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return ResponseEntity.ok(result.getpayload());
    }


    // ADD ITEM TO CART
    @PostMapping("/{userId}/items")
    public ResponseEntity<Object> addToCart(
            @PathVariable int userId,
            @RequestBody AddCartItemRequest request) {

        Result<CartItem> result =
                service.addToCart(
                        userId,
                        request.getProductId(),
                        request.getQuantity()
                );

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return ResponseEntity.ok(result.getpayload());
    }


    // UPDATE QUANTITY
    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<Object> updateQuantity(
            @PathVariable int userId,
            @PathVariable int productId,
            @RequestBody UpdateCartItemRequest request) {

        Result<CartItem> result =
                service.updateQuantity(
                        userId,
                        productId,
                        request.getQuantity()
                );

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return ResponseEntity.ok(result.getpayload());
    }


    // REMOVE ONE ITEM
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Object> removeItemFromCart(
            @PathVariable int userId,
            @PathVariable int productId) {

        Result<Void> result =
                service.removeFromCart(userId, productId);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // CLEAR ENTIRE CART
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> clearCart(
            @PathVariable int userId) {

        Result<Void> result =
                service.clearCart(userId);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}