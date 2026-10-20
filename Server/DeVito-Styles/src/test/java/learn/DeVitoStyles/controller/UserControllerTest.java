package learn.DeVitoStyles.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.domain.UserService;
import learn.DeVitoStyles.domain.security.JwtUtil;
import learn.DeVitoStyles.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService service;

    @Mock
    private JwtUtil jwtUtil;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new UserController(service, jwtUtil)).build();
    }

    private static User user(int id) {
        return new User(id, "John", "Smith", "john@example.com", "jsmith", "secret",
                "123 Main St", "555-1234", "CUSTOMER");
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

    // ---------- POST /{userId}/profile-picture ----------

    @Test
    void uploadProfilePicture_returns200WithProfileUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", new byte[]{1, 2, 3});
        User updated = user(4);
        updated.setProfileUrl("https://cdn.example/avatar.png");
        when(service.uploadProfilePicture(eq(4), any(MultipartFile.class))).thenReturn(updated);

        mvc.perform(multipart("/api/user/4/profile-picture").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("https://cdn.example/avatar.png"));

        ArgumentCaptor<MultipartFile> captor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(service).uploadProfilePicture(eq(4), captor.capture());
        assertThat(captor.getValue().getOriginalFilename()).isEqualTo("avatar.png");
        assertThat(captor.getValue().getBytes()).containsExactly(1, 2, 3);
    }

    @Test
    void uploadProfilePicture_whenFilePartMissing_returns400WithoutCallingService() throws Exception {
        mvc.perform(multipart("/api/user/4/profile-picture"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    // ---------- POST /login ----------

    @Test
    void login_whenSuccess_returns200WithTokenAndUserInfo() throws Exception {
        User dbUser = user(4);
        when(service.authenticate(any(User.class))).thenReturn(success(dbUser));
        when(jwtUtil.generateToken(dbUser)).thenReturn("jwt-token");

        mvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"jsmith\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.userId").value(4))
                .andExpect(jsonPath("$.username").value("jsmith"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.password").doesNotExist());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(service).authenticate(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("jsmith");
        assertThat(captor.getValue().getPassword()).isEqualTo("secret");
        verify(jwtUtil).generateToken(dbUser);
    }

    @Test
    void login_whenUserNotFound_returns404AndNoToken() throws Exception {
        when(service.authenticate(any(User.class))).thenReturn(failure("User does not exist", ResultType.NOT_FOUND));

        mvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ghost\",\"password\":\"x\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("User does not exist"));

        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_whenInvalidCredentials_returns401AndNoToken() throws Exception {
        when(service.authenticate(any(User.class))).thenReturn(failure("Incorrect password", ResultType.INVALID));

        mvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"jsmith\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$[0]").value("Incorrect password"));

        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_whenOtherFailure_returns400AndNoToken() throws Exception {
        when(service.authenticate(any(User.class))).thenReturn(failure("Something went wrong", ResultType.CONFLICT));

        mvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"jsmith\",\"password\":\"pw\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Something went wrong"));

        verifyNoInteractions(jwtUtil);
    }

    // ---------- POST /register ----------

    @Test
    void create_whenSuccess_returns201WithUser() throws Exception {
        when(service.create(any(User.class))).thenReturn(success(user(9)));

        mvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user(0))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(9))
                .andExpect(jsonPath("$.username").value("jsmith"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(service).create(captor.capture());
        assertThat(captor.getValue()).isEqualTo(user(0));
        assertThat(captor.getValue().getPassword()).isEqualTo("secret");
    }

    @Test
    void create_whenInvalid_returns400WithMessages() throws Exception {
        when(service.create(any(User.class))).thenReturn(failure("Email is required", ResultType.INVALID));

        mvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user(0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Email is required"));
    }

    @Test
    void create_whenConflict_returns400() throws Exception {
        // UserController maps every failure on /register to 400, including duplicates.
        when(service.create(any(User.class))).thenReturn(failure("Username already taken", ResultType.CONFLICT));

        mvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user(0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Username already taken"));
    }

    // ---------- GET /{id} ----------

    @Test
    void findById_whenFound_returns200WithUser() throws Exception {
        when(service.findById(4)).thenReturn(success(user(4)));

        mvc.perform(get("/api/user/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(4))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        verify(service).findById(4);
    }

    @Test
    void findById_whenNotFound_returns404() throws Exception {
        when(service.findById(99)).thenReturn(failure("User not found", ResultType.NOT_FOUND));

        mvc.perform(get("/api/user/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("User not found"));
    }

    @Test
    void findById_whenInvalid_returns400() throws Exception {
        when(service.findById(0)).thenReturn(failure("User id must be positive", ResultType.INVALID));

        mvc.perform(get("/api/user/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("User id must be positive"));
    }

    // ---------- PUT /{id} ----------

    @Test
    void update_whenSuccess_returns200WithUser() throws Exception {
        User updated = user(4);
        updated.setPhone("555-9999");
        when(service.update(any(User.class))).thenReturn(success(updated));

        mvc.perform(put("/api/user/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(4))
                .andExpect(jsonPath("$.phone").value("555-9999"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(service).update(captor.capture());
        assertThat(captor.getValue()).isEqualTo(updated);
    }

    @Test
    void update_whenPathIdMismatch_returns409WithoutCallingService() throws Exception {
        mvc.perform(put("/api/user/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user(4))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$[0]").value("Path ID and User ID must match."));

        verifyNoInteractions(service);
    }

    @Test
    void update_whenNotFound_returns404() throws Exception {
        when(service.update(any(User.class))).thenReturn(failure("User not found", ResultType.NOT_FOUND));

        mvc.perform(put("/api/user/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user(4))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("User not found"));
    }

    @Test
    void update_whenInvalid_returns400() throws Exception {
        when(service.update(any(User.class))).thenReturn(failure("Email is required", ResultType.INVALID));

        mvc.perform(put("/api/user/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user(4))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Email is required"));
    }

    // ---------- GET / ----------

    @Test
    void findAll_returns200WithUsers() throws Exception {
        when(service.findAll()).thenReturn(success(List.of(user(1), user(2))));

        mvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[1].userId").value(2));

        verify(service).findAll();
    }
}
