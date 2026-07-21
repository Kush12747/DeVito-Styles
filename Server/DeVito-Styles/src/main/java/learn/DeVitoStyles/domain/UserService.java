package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.UserRepository;
import learn.DeVitoStyles.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, CloudinaryService service) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = service;
    }

    @Transactional
    public User uploadProfilePicture(int userId, MultipartFile file) throws IOException {
        User user = repository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = cloudinaryService.uploadImage(file);

        repository.updateProfileUrl(userId, imageUrl);

        user.setProfileUrl(imageUrl);

        return user;
    }

    // =========================
    // AUTHENTICATION (LOGIN)
    // =========================
    public Result<User> authenticate(User proposedLoginUser) throws DataAccessException {
        Result<User> result = new Result<>();

        Optional<User> userFromDatabase = repository.findByUsername(proposedLoginUser.getUsername());

        if (userFromDatabase.isEmpty()) {
            result.addErrorMessage("User does not exist", ResultType.NOT_FOUND);
            return result;
        }

        if (proposedLoginUser.getUsername() == null || proposedLoginUser.getUsername().isBlank()) {
            result.addErrorMessage("Username cannot be blank", ResultType.INVALID);
            return result;
        }

        if (proposedLoginUser.getPassword() == null || proposedLoginUser.getPassword().isBlank()) {
            result.addErrorMessage("Password cannot be blank", ResultType.INVALID);
            return result;
        }

        User dbUser = userFromDatabase.get();

        // Secure password check
        if (!passwordEncoder.matches(proposedLoginUser.getPassword(), dbUser.getPassword())) {
            result.addErrorMessage("Incorrect password", ResultType.INVALID);
            return result;
        }

        result.setpayload(dbUser);
        return result;
    }

    // =========================
    // CREATE USER (REGISTER)
    // =========================
    public Result<User> create(User user) throws DataAccessException {
        Result<User> result = new Result<>();

        if (user.getUsername().isBlank()) {
            result.addErrorMessage("Username cannot be blank", ResultType.INVALID);
        }

        if (user.getPassword().isBlank()) {
            result.addErrorMessage("Password cannot be blank", ResultType.INVALID);
        }

        Optional<User> existing = repository.findByUsername(user.getUsername());

        if (existing.isPresent()) {
            result.addErrorMessage("Username is already taken", ResultType.INVALID);
        }

        if (!result.isSuccess()) {
            return result;
        }

        // Real Password Hashing
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User created = repository.create(user);
        result.setpayload(created);

        return result;
    }

    // =========================
    // GET USER PROFILE
    // =========================
    public Result<User> findById(int userId) {
        Result<User> result = new Result<>();

        Optional<User> user = repository.findById(userId);

        if (userId <= 0) {
            result.addErrorMessage("Invalid user ID", ResultType.INVALID);
            return result;
        }

        if (user.isEmpty()) {
            result.addErrorMessage("User not found", ResultType.NOT_FOUND);
            return result;
        }

        result.setpayload(user.get());
        return result;
    }

    // =========================
    // UPDATE USER PROFILE
    // =========================
    public Result<User> update(User user) {
        Result<User> result = new Result<>();

        Optional<User> existing = repository.findById(user.getUserId());

        if (user.getUserId() <= 0) {
            result.addErrorMessage("Invalid user ID", ResultType.INVALID);
            return result;
        }

        if (existing.isEmpty()) {
            result.addErrorMessage("User not found", ResultType.NOT_FOUND);
            return result;
        }

        if (!result.isSuccess()) {
            return result;
        }

        repository.update(user);
        result.setpayload(user);

        return result;
    }

    // =========================
    // ADMIN: GET ALL USERS
    // =========================
    public Result<List<User>> findAll() {
        Result<List<User>> result = new Result<>();

        List<User> users = repository.findAll();

        result.setpayload(users);

        return result;
    }
}