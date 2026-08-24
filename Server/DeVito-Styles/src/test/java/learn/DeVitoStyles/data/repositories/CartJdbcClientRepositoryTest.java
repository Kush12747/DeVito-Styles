package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.models.ShoppingCart.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CartJdbcClientRepositoryTest {

    @Autowired
    private CartJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindByUserId() {

        Cart cart = repository.findByUserId(2);

        assertNotNull(cart);
        assertEquals(1, cart.getCartId());
        assertEquals(2, cart.getUserId());
    }

    @Test
    void shouldNotFindInvalidUserId() {

        Cart cart = repository.findByUserId(999);

        assertNull(cart);
    }

    @Test
    void shouldFindById() {

        Cart cart = repository.findById(2);

        assertNotNull(cart);
        assertEquals(2, cart.getCartId());
        assertEquals(3, cart.getUserId());
    }

    @Test
    void shouldNotFindInvalidCartId() {

        Cart cart = repository.findById(999);

        assertNull(cart);
    }

    @Test
    void shouldAdd() {

        Cart cart = new Cart();
        cart.setUserId(2);

        Cart added = repository.add(cart);

        assertNotNull(added);
        assertTrue(added.getCartId() > 0);
        assertEquals(2, added.getUserId());

        Cart found = repository.findById(added.getCartId());

        assertNotNull(found);
        assertEquals(2, found.getUserId());
    }

    @Test
    void shouldNotDeleteInvalidId() {

        assertFalse(repository.deleteById(999));
    }
}