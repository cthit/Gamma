package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.domain.UnencryptedPassword;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void create(User user);
    void save(User user);
    void delete(UserId userId) throws UserNotFoundException;
    void setPassword(UnencryptedPassword password);

    List<User> getAll();
    Optional<User> get(UserId userId);

    class UserNotFoundException extends Exception { }

}
