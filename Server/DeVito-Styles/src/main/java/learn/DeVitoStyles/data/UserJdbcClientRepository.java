package learn.DeVitoStyles.data;

import learn.DeVitoStyles.data.mappers.UserMapper;
import learn.DeVitoStyles.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserJdbcClientRepository implements UserRepository {
    private final JdbcClient jdbcClient;

    public UserJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public boolean updateProfileUrl(int userId, String url) {
        final String sql = """
                update users set profile_picture_url = ?
                where user_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(url)
                .param(userId)
                .update() > 0;
    }

    // Register user (signup)
    @Override
    public User create(User user) throws DataAccessException {
        final String sql = """
               INSERT INTO users (first_name, last_name, email, username, password, address, phone, role)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?);
               """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(user.getFirstName())
                .param(user.getLastName())
                .param(user.getEmail())
                .param(user.getUsername())
                .param(user.getPassword())
                .param(user.getAddress())
                .param(user.getPhone())
                .param(user.getRole())
                .update(keyHolder, "user_id");

        if (rowsAffected == 0) {
            return null;
        }

        user.setUserId(keyHolder.getKey().intValue());

        return user;
    }

    // Login find by username (login system)
    @Override
    public Optional<User> findByUsername(String username) {
        final String sql = """
                SELECT * FROM users
                WHERE username = ?;
                """;

        return jdbcClient.sql(sql)
                .param(username)
                .query(new UserMapper())
                .optional();
    }

    // find by id Profile (load profile)
    @Override
    public Optional<User> findById(int userId) {
        final String sql = """
                SELECT * FROM users
                WHERE user_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(userId)
                .query(new UserMapper())
                .optional();
    }

    // update User Profile (edit profile)
    @Override
    public boolean update(User user) {
        final String sql = """
                UPDATE users SET
                    first_name = ?,
                    last_name = ?,
                    email = ?,
                    username = ?,
                    password = ?,
                    address = ?,
                    phone = ?,
                    role = ?
                    profile_picture_url = ?
                WHERE user_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(user.getFirstName())
                .param(user.getLastName())
                .param(user.getEmail())
                .param(user.getUsername())
                .param(user.getPassword())
                .param(user.getAddress())
                .param(user.getPhone())
                .param(user.getRole())
                .param(user.getUserId())
                .param(user.getProfileUrl())
                .update() > 0;
    }

    // Get all users (user table view)
    @Override
    public List<User> findAll() {
        final String sql = """
                SELECT * FROM users;
                """;

        return jdbcClient.sql(sql)
                .query(new UserMapper())
                .list();
    }


}
