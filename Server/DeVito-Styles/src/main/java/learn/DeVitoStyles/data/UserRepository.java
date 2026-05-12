package learn.DeVitoStyles.data;

import learn.DeVitoStyles.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findById(int userId);

    boolean update(User user);

    List<User> findAll();
}
