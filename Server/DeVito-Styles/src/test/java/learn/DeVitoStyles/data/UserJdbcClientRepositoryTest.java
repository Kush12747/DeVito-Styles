package learn.DeVitoStyles.data;

import learn.DeVitoStyles.data.repositories.UserJdbcClientRepository;
import learn.DeVitoStyles.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserJdbcClientRepositoryTest {

    @Autowired
    private UserJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@email.com");
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setAddress("123 Street");
        user.setPhone("1234567890");
        user.setRole("CUSTOMER");

        User created = repository.create(user);

        assertNotNull(created);
        assertTrue(created.getUserId() > 0);
    }

    @Test
    void shouldNotAllowDuplicateUsername() {
        User user1 = new User();
        user1.setFirstName("Test");
        user1.setLastName("User");
        user1.setEmail("test1@email.com");
        user1.setUsername("duplicateUser");
        user1.setPassword("password123");
        user1.setAddress("123 Street");
        user1.setPhone("1111111111");
        user1.setRole("CUSTOMER");

        repository.create(user1);

        User user2 = new User();
        user2.setFirstName("Test2");
        user2.setLastName("User2");
        user2.setEmail("test2@email.com");
        user2.setUsername("duplicateUser"); // same username
        user2.setPassword("password123");
        user2.setAddress("123 Street");
        user2.setPhone("2222222222");
        user2.setRole("CUSTOMER");

        assertThrows(Exception.class, () -> repository.create(user2));
    }

    @Test
    void shouldFindByUsername() {
        Optional<User> result = repository.findByUsername("admin1");

        assertTrue(result.isPresent());
        assertEquals("admin1", result.get().getUsername());
    }

    @Test
    void shouldReturnEmptyWhenUsernameNotFound() {
        Optional<User> result = repository.findByUsername("doesNotExistUser");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindById() {
        Optional<User> byUsername = repository.findByUsername("admin1");
        assertTrue(byUsername.isPresent());

        int id = byUsername.get().getUserId();

        Optional<User> result = repository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getUserId());
    }

    @Test
    void shouldNotFindById() {
        Optional<User> result = repository.findById(9999);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUpdateUser() {
        Optional<User> optionalUser = repository.findByUsername("admin1");
        assertTrue(optionalUser.isPresent());

        User user = optionalUser.get();
        user.setFirstName("UpdatedName");

        boolean success = repository.update(user);

        assertTrue(success);

        Optional<User> updated = repository.findById(user.getUserId());
        assertTrue(updated.isPresent());
        assertEquals("UpdatedName", updated.get().getFirstName());
    }

    @Test
    void shouldReturnFalseWhenUpdatingNonExistentUser() {
        User fakeUser = new User();
        fakeUser.setUserId(99999);
        fakeUser.setFirstName("Ghost");
        fakeUser.setLastName("User");
        fakeUser.setEmail("ghost@email.com");
        fakeUser.setUsername("ghost");
        fakeUser.setPassword("password");
        fakeUser.setAddress("Nowhere");
        fakeUser.setPhone("0000000000");
        fakeUser.setRole("CUSTOMER");

        boolean result = repository.update(fakeUser);

        assertFalse(result);
    }

    @Test
    void shouldFindAllUsers() {
        List<User> users = repository.findAll();

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

}