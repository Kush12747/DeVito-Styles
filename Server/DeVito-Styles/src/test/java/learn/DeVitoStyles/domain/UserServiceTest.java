package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.UserRepository;
import learn.DeVitoStyles.models.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserServiceTest {

    @Autowired
    UserService service;

    @MockBean
    UserRepository repository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void authenticateHappyPath() {
        User loginInput = new User();
        loginInput.setUsername("jdoe");
        loginInput.setPassword("password123");

        User dbUser = new User();
        dbUser.setUserId(1);
        dbUser.setUsername("jdoe");
        dbUser.setPassword("$2a$10$yfcaphpV5EtBieqi8XsTD.BEvatjKxttE4mWXJ8Aq8aM.XWYlgov.");

        when(repository.findByUsername("jdoe"))
                .thenReturn(Optional.of(dbUser));

        when(passwordEncoder.matches("password123", "$2a$10$yfcaphpV5EtBieqi8XsTD.BEvatjKxttE4mWXJ8Aq8aM.XWYlgov."))
                .thenReturn(true);

        Result<User> result = service.authenticate(loginInput);

        assertTrue(result.isSuccess());
        assertNotNull(result.getpayload());
        assertEquals("jdoe", result.getpayload().getUsername());
    }

    @Test
    void authenticateUserNotFound() {
        User loginInput = new User();
        loginInput.setUsername("missing");
        loginInput.setPassword("password123");

        when(repository.findByUsername("missing")).thenReturn(Optional.empty());

        Result<User> result = service.authenticate(loginInput);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("User does not exist"));
    }

    @Test
    void authenticateWrongPassword() {
        User loginInput = new User();
        loginInput.setUsername("jdoe");
        loginInput.setPassword("wrong");

        User dbUser = new User();
        dbUser.setUsername("jdoe");
        dbUser.setPassword("correct");

        when(repository.findByUsername("jdoe"))
                .thenReturn(Optional.of(dbUser));

        Result<User> result = service.authenticate(loginInput);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Incorrect password"));
    }

    @Test
    void createHappyPath() {
        User input = new User();
        input.setUsername("newuser");
        input.setPassword("pass123");
        input.setEmail("new@email.com");

        when(repository.findByUsername("newuser")).thenReturn(Optional.empty());

        User saved = new User();
        saved.setUserId(1);
        saved.setUsername("newuser");

        when(repository.create(input)).thenReturn(saved);

        Result<User> result = service.create(input);

        assertTrue(result.isSuccess());
        assertNotNull(result.getpayload());
        assertEquals("newuser", result.getpayload().getUsername());
    }

    @Test
    void createDuplicateUsername() {
        User input = new User();
        input.setUsername("jdoe");
        input.setPassword("pass123");
        input.setEmail("email@test.com");

        User existing = new User();
        existing.setUsername("jdoe");

        when(repository.findByUsername("jdoe"))
                .thenReturn(Optional.of(existing));

        Result<User> result = service.create(input);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Username is already taken"));
    }


    @Test
    void findByIdHappyPath() {
        User user = new User();
        user.setUserId(1);

        when(repository.findById(1)).thenReturn(Optional.of(user));

        Result<User> result = service.findById(1);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getUserId());
    }

    @Test
    void shouldNotFindById() {
        when(repository.findById(9999)).thenReturn(Optional.empty());

        Result<User> result = service.findById(9999);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("User not found"));
    }

    @Test
    void updateHappyPath() {
        User existing = new User();
        existing.setUserId(1);

        User update = new User();
        update.setUserId(1);
        update.setUsername("updated");

        when(repository.findById(1)).thenReturn(Optional.of(existing));
        when(repository.update(update)).thenReturn(true);

        Result<User> result = service.update(update);

        assertTrue(result.isSuccess());
        assertEquals("updated", result.getpayload().getUsername());
    }

    @Test
    void updateUserNotFound() {
        User update = new User();
        update.setUserId(999);

        when(repository.findById(999)).thenReturn(Optional.empty());

        Result<User> result = service.update(update);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("User not found"));
    }
    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(List.of(new User(), new User()));

        Result<List<User>> result = service.findAll();
        assertEquals(2, result.getpayload().size());
    }
}