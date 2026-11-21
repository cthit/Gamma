package it.chalmers.gamma.app.signin.domain;

import it.chalmers.gamma.app.user.domain.UserId;
import java.util.List;

public interface PasskeyRepository {

  List<Passkey> getAll(UserId userId);

  boolean hasAny(UserId userId);

  boolean delete(UserId userId, String credentialId);

  void deleteAll(UserId userId);
}
