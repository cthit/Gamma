package it.chalmers.gamma.app.user.domain;

import it.chalmers.gamma.app.common.Email;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void create(GammaUser user, UnencryptedPassword password)
            throws UserAlreadyExistsException, CidAlreadyInUseException, EmailAlreadyInUseException;
    void save(GammaUser user);
    void delete(UserId userId) throws UserNotFoundException;

    List<GammaUser> getAll();
    Optional<GammaUser> get(UserId userId);
    Optional<GammaUser> get(Cid cid);
    Optional<GammaUser> get(Email email);

    boolean checkPassword(UserId userId, UnencryptedPassword password) throws UserNotFoundException;
    void setPassword(UserId userId, UnencryptedPassword newPassword) throws UserNotFoundException;
    void acceptUserAgreement(UserId userId) throws UserNotFoundException;

    class UserNotFoundException extends RuntimeException { }

    /**
     * A user with the given id already exists. Use save instead.
     */
    class UserAlreadyExistsException extends RuntimeException { }

    class CidAlreadyInUseException extends Exception { }
    class EmailAlreadyInUseException extends Exception { }

}
