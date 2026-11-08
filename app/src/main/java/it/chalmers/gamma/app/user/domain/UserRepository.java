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

  /**
   * The id of the user with the cid, case insensitively, whoever is asking. Used to find the user
   * of someone who isn't signed in yet.
   */
  Optional<UserId> findId(Cid cid);

  /** The id of the user with the email, whoever is asking. See {@link #findId(Cid)}. */
  Optional<UserId> findId(Email email);

  boolean checkPassword(UserId userId, UnencryptedPassword password) throws UserNotFoundException;

  void setPassword(UserId userId, UnencryptedPassword newPassword) throws UserNotFoundException;

  boolean hasPassword(UserId userId) throws UserNotFoundException;

  void removePassword(UserId userId) throws UserNotFoundException;

  /**
   * Whether the user has allowed resetting how they sign in through a link mailed to them. Admins
   * can never do that, whatever this returns.
   */
  boolean isEmailRecoveryEnabled(UserId userId) throws UserNotFoundException;

  void setEmailRecoveryEnabled(UserId userId, boolean enabled) throws UserNotFoundException;

  void acceptUserAgreement(UserId userId) throws UserNotFoundException;

  void setEmail(UserId userId, Email email)
      throws UserNotFoundException, EmailAlreadyInUseException;

  class UserNotFoundException extends RuntimeException {}

  /** A user with the given id already exists. Use save instead. */
  class UserAlreadyExistsException extends RuntimeException {}

  class CidAlreadyInUseException extends Exception {}

  class EmailAlreadyInUseException extends Exception {}
}
