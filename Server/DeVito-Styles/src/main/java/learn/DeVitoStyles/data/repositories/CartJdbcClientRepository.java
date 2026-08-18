package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.CartRepository;
import learn.DeVitoStyles.data.mappers.CartMapper;
import learn.DeVitoStyles.models.ShoppingCart.Cart;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CartJdbcClientRepository implements CartRepository {

    private final JdbcClient jdbcClient;
    private final CartMapper mapper = new CartMapper();
    final String BASE_SELECT = """
            SELECT * FROM carts
            """;

    public CartJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Cart findByUserId(int userId) {
        final String sql = BASE_SELECT + " WHERE user_id = :user_id;";

        return jdbcClient.sql(sql)
                .param("user_id", userId)
                .query(mapper)
                .optional()
                .orElse(null);
    }

    @Override
    public Cart findById(int cartId) {
        final String sql = BASE_SELECT + " WHERE cart_id = :cart_id;";

        return jdbcClient.sql(sql)
                .param("cart_id", cartId)
                .query(mapper)
                .optional()
                .orElse(null);
    }

    @Override
    public Cart add(Cart cart) {
        final String sql = "INSERT INTO carts (user_id) VALUES (:user_id)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(cart.getCartId())
                .update(keyHolder, "user_id");

        if (rowsAffected == 0) {
            return null;
        }

        cart.setCartId(keyHolder.getKey().intValue());
        return cart;
    }

    @Override
    public boolean deleteById(int cartId) {
        final String sql = "DELETE FROM carts WHERE cart_id = :cart_id;";

        return jdbcClient.sql(sql)
                .param("cart_id", cartId)
                .update() > 0;
    }
}
