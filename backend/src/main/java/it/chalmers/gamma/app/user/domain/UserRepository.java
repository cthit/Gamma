package it.chalmers.gamma.app.user.domain;

import it.chalmers.gamma.app.common.Email;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void create(User user, UnencryptedPassword password);
    void save(User user);
    void delete(UserId userId) throws UserNotFoundRuntimeException;

    List<User> getAll();
    Optional<User> get(UserId userId);
    Optional<User> get(Cid cid);
    Optional<User> get(Email email);

    boolean checkPassword(UserId userId, UnencryptedPassword password) throws UserNotFoundRuntimeException;
    void setPassword(UserId userId, UnencryptedPassword newPassword) throws UserNotFoundRuntimeException;
    void acceptUserAgreement(UserId userId) throws UserNotFoundRuntimeException;

    class UserNotFoundRuntimeException extends RuntimeException { }

}
