package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.UnencryptedPassword;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void create(User user, UnencryptedPassword unencryptedPassword);
    void save(User user);
    void delete(UserId userId) throws UserNotFoundException;
    void setPassword(UserId userId, UnencryptedPassword password);

    List<User> getAll();
    Optional<User> get(UserId userId);
    Optional<User> get(Cid cid);
    Optional<User> get(Email email);

    class UserNotFoundException extends Exception { }

}
