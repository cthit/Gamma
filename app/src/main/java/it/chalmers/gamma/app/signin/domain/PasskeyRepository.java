package it.chalmers.gamma.app.signin.domain;

import it.chalmers.gamma.app.user.domain.UserId;
import java.util.List;

public interface PasskeyRepository {

  List<Passkey> getAll(UserId userId);

  boolean hasAny(UserId userId);

  /**
   * Removes the passkey if it belongs to the user.
   *
   * @return whether a passkey was removed
   */
  boolean delete(UserId userId, String credentialId);

  void deleteAll(UserId userId);
}
