package it.chalmers.gamma.app.port.repository;

import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.user.UnencryptedPassword;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void save(User user);
    void delete(UserId userId) throws UserNotFoundException;
    void setPassword(UserId userId, UnencryptedPassword password);

    List<User> getAll();
    Optional<User> get(UserId userId);
    Optional<User> get(Cid cid);
    Optional<User> get(Email email);

    class UserNotFoundException extends Exception { }

}
