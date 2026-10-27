package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.UserRepository;
import learn.DeVitoStyles.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String HASH = "$2a$10$yfcaphpV5EtBieqi8XsTD.BEvatjKxttE4mWXJ8Aq8aM.XWYlgov.";

    @Mock private UserRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private CloudinaryService cloudinaryService;

    @InjectMocks private UserService service;

    // =========================
    // AUTHENTICATE
    // =========================

    @Test
    void shouldAuthenticate() {
        User dbUser = makeUser(1, "jdoe", HASH);

        when(repository.findByUsername("jdoe")).thenReturn(Optional.of(dbUser));
        when(passwordEncoder.matches("password123", HASH)).thenReturn(true);

        Result<User> result = service.authenticate(makeLogin("jdoe", "password123"));

        assertTrue(result.isSuccess());
        assertSame(dbUser, result.getpayload());
    }

    @Test
    void shouldNotAuthenticateMissingUser() {
        when(repository.findByUsername("missing")).thenReturn(Optional.empty());

        Result<User> result = service.authenticate(makeLogin("missing", "password123"));

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("User does not exist"), result.getMessages());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldNotAuthenticateBlankUsername() {
        Result<User> result = service.authenticate(makeLogin(" ", "password123"));

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Username cannot be blank"), result.getMessages());
        verifyNoInteractions(repository, passwordEncoder);
    }

    @Test
    void shouldNotAuthenticateNullUsername() {
        Result<User> result = service.authenticate(makeLogin(null, "password123"));

        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Username cannot be blank"), result.getMessages());
        verifyNoInteractions(repository, passwordEncoder);
    }

    @Test
    void shouldNotAuthenticateBlankPassword() {
        Result<User> result = service.authenticate(makeLogin("jdoe", " "));

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Password cannot be blank"), result.getMessages());
        verifyNoInteractions(repository, passwordEncoder);
    }

    @Test
    void shouldNotAuthenticateWrongPassword() {
        when(repository.findByUsername("jdoe")).thenReturn(Optional.of(makeUser(1, "jdoe", HASH)));
        when(passwordEncoder.matches("wrong", HASH)).thenReturn(false);

        Result<User> result = service.authenticate(makeLogin("jdoe", "wrong"));

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Incorrect password"), result.getMessages());
        assertNull(result.getpayload());
    }

    // =========================
    // CREATE
    // =========================

    @Test
    void shouldCreateWithHashedPassword() {
        User input = makeUser(0, "newuser", "pass123");
        input.setEmail("new@email.com");
        User saved = makeUser(1, "newuser", HASH);

        when(repository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn(HASH);
        when(repository.create(input)).thenReturn(saved);

        Result<User> result = service.create(input);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getUserId());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).create(captor.capture());
        assertEquals(HASH, captor.getValue().getPassword());
    }

    @Test
    void shouldNotCreateDuplicateUsername() {
        User input = makeUser(0, "jdoe", "pass123");
        when(repository.findByUsername("jdoe")).thenReturn(Optional.of(makeUser(1, "jdoe", HASH)));

        Result<User> result = service.create(input);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Username is already taken"), result.getMessages());
        verify(repository, never()).create(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldNotCreateBlankUsername() {
        User input = makeUser(0, " ", "pass123");

        Result<User> result = service.create(input);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Username cannot be blank"), result.getMessages());
        verify(repository, never()).create(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldNotCreateBlankPassword() {
        User input = makeUser(0, "newuser", "");

        Result<User> result = service.create(input);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Password cannot be blank"), result.getMessages());
        verify(repository, never()).create(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldReportAllCreateErrors() {
        User input = makeUser(0, "", "");
        when(repository.findByUsername("")).thenReturn(Optional.of(new User()));

        Result<User> result = service.create(input);

        assertFalse(result.isSuccess());
        assertEquals(List.of(
                "Username cannot be blank",
                "Password cannot be blank",
                "Username is already taken"
        ), result.getMessages());
        verify(repository, never()).create(any());
    }

    // =========================
    // FIND BY ID
    // =========================

    @Test
    void shouldFindById() {
        User user = makeUser(1, "jdoe", HASH);
        when(repository.findById(1)).thenReturn(Optional.of(user));

        Result<User> result = service.findById(1);

        assertTrue(result.isSuccess());
        assertSame(user, result.getpayload());
    }

    @Test
    void shouldNotFindById() {
        when(repository.findById(9999)).thenReturn(Optional.empty());

        Result<User> result = service.findById(9999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("User not found"), result.getMessages());
    }

    @Test
    void shouldNotFindByInvalidId() {
        Result<User> result = service.findById(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Invalid user ID"), result.getMessages());
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void shouldUpdateKeepingStoredPasswordWhenNoneSent() {
        User current = makeUser(1, "jdoe", HASH);
        current.setRole("CUSTOMER");
        User update = makeUser(1, "updated", null);
        when(repository.findById(1)).thenReturn(Optional.of(current));
        when(repository.update(update)).thenReturn(true);

        Result<User> result = service.update(update);

        assertTrue(result.isSuccess());
        assertEquals("updated", result.getpayload().getUsername());
        assertEquals(HASH, result.getpayload().getPassword());
        verify(repository).update(update);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldHashNewPasswordOnUpdate() {
        User current = makeUser(1, "jdoe", HASH);
        User update = makeUser(1, "jdoe", "new-password");
        when(repository.findById(1)).thenReturn(Optional.of(current));
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");
        when(repository.update(update)).thenReturn(true);

        Result<User> result = service.update(update);

        assertTrue(result.isSuccess());
        assertEquals("new-hash", result.getpayload().getPassword());
    }

    @Test
    void shouldNotLetUpdateChangeRole() {
        User current = makeUser(1, "jdoe", HASH);
        current.setRole("CUSTOMER");
        User update = makeUser(1, "jdoe", null);
        update.setRole("ADMIN");
        when(repository.findById(1)).thenReturn(Optional.of(current));
        when(repository.update(update)).thenReturn(true);

        Result<User> result = service.update(update);

        assertTrue(result.isSuccess());
        assertEquals("CUSTOMER", result.getpayload().getRole());
    }

    @Test
    void shouldNotUpdateWhenRepositoryFails() {
        User update = makeUser(1, "jdoe", null);
        when(repository.findById(1)).thenReturn(Optional.of(makeUser(1, "jdoe", HASH)));
        when(repository.update(update)).thenReturn(false);

        Result<User> result = service.update(update);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Unable to update user"), result.getMessages());
    }

    @Test
    void shouldNotUpdateMissingUser() {
        User update = makeUser(999, "ghost", HASH);
        when(repository.findById(999)).thenReturn(Optional.empty());

        Result<User> result = service.update(update);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("User not found"), result.getMessages());
        verify(repository, never()).update(any());
    }

    @Test
    void shouldNotUpdateInvalidId() {
        User update = makeUser(0, "jdoe", HASH);

        Result<User> result = service.update(update);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Invalid user ID"), result.getMessages());
        verifyNoInteractions(repository);
    }

    // =========================
    // FIND ALL
    // =========================

    @Test
    void shouldFindAll() {
        when(repository.findAll()).thenReturn(List.of(new User(), new User()));

        Result<List<User>> result = service.findAll();

        assertTrue(result.isSuccess());
        assertEquals(2, result.getpayload().size());
    }

    // =========================
    // UPLOAD PROFILE PICTURE
    // =========================

    @Test
    void shouldUploadProfilePicture() throws IOException {
        User user = makeUser(1, "jdoe", HASH);
        MultipartFile file = mock(MultipartFile.class);
        String url = "https://cdn.example.com/u1.png";

        when(repository.findById(1)).thenReturn(Optional.of(user));
        when(cloudinaryService.uploadImage(file)).thenReturn(url);

        User result = service.uploadProfilePicture(1, file);

        assertEquals(url, result.getProfileUrl());
        verify(repository).updateProfileUrl(1, url);
    }

    @Test
    void shouldNotUploadProfilePictureForMissingUser() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(repository.findById(999)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.uploadProfilePicture(999, file));

        assertEquals("User not found", ex.getMessage());
        verify(cloudinaryService, never()).uploadImage(any());
        verify(repository, never()).updateProfileUrl(anyInt(), anyString());
    }

    // =========================
    // HELPERS
    // =========================

    private User makeLogin(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    private User makeUser(int userId, String username, String password) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
}
