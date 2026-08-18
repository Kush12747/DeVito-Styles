package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.CartItemRepository;
import learn.DeVitoStyles.data.mappers.CartItemMapper;
import learn.DeVitoStyles.models.ShoppingCart.CartItem;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CartItemJdbcClientRepository implements CartItemRepository {

    private final JdbcClient jdbcClient;
    private final CartItemMapper itemMapper = new CartItemMapper();
    private final String BASE_SELECT = """
            SELECT
                ci.cart_item_id,
                ci.cart_id,
                ci.quantity,
           
                p.product_id,
                p.category_id,
                p.name,
                p.description,
                p.price,
                p.stock_quantity,
                p.image_url,
                p.is_featured,
                p.is_active
            
            FROM cart_items ci
            JOIN products p
                ON ci.product_id = p.product_id;
            """;

    public CartItemJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<CartItem> findByCartId(int cartId) {
        final String sql = BASE_SELECT + " WHERE ci.cart_id = :cart_id";

        return jdbcClient.sql(sql)
                .param("cart_id", cartId)
                .query(itemMapper)
                .list();
    }

    // Check the cart if item exists and update count
    @Override
    public CartItem findByCartIdAndProductId(int cartId, int productId) {
        final String sql = BASE_SELECT + """
                WHERE ci.cart_id = :cart_id
                AND p.product_id = :product_id;
                """;

        return jdbcClient.sql(sql)
                .param("cart_id", cartId)
                .param("product_id", productId)
                .query(itemMapper)
                .optional()
                .orElse(null);
    }

    @Override
    public CartItem add(CartItem item) {
        final String sql = """
                INSERT INTO cart_items
                (cart_id, product_id, quantity)
                VALUES
                (:cart_id, :product_id, :quantity);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(":cart_id", item.getCartId())
                .param(":product_id", item.getProduct().getProductId())
                .param(":quantity", item.getQuantity())
                .update(keyHolder, "cart_item_id");

        if (rowsAffected == 0) {
            return null;
        }

        item.setCartItemId(keyHolder.getKey().intValue());

        return item;
    }

    @Override
    public boolean update(CartItem cartItem) {
        final String sql = """
                UPDATE cart_items
                SET quantity = :quantity
                WHERE cart_item_id = :cart_item_id;
                """;

        return jdbcClient.sql(sql)
                .param("quantity", cartItem.getQuantity())
                .param("cart_item_id", cartItem.getCartItemId())
                .update() > 0;
    }

    @Override
    public boolean deleteById(int cartItemId) {
        final String sql = """
            DELETE FROM cart_items
            WHERE cart_item_id = :cart_item_id;
            """;

        return jdbcClient.sql(sql)
                .param("cart_item_id", cartItemId)
                .update() > 0;
    }

    @Override
    public boolean deleteByCartId(int cartId) {
        final String sql = """
            DELETE FROM cart_items
            WHERE cart_id = :cart_id;
            """;

        return jdbcClient.sql(sql)
                .param("cart_id", cartId)
                .update() > 0;
    }
}
