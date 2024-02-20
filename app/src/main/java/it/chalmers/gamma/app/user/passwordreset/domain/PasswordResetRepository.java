package it.chalmers.gamma.app.user.passwordreset.domain;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.UserId;
import java.util.Optional;

public interface PasswordResetRepository {

  PasswordResetToken createNewToken(Email email) throws UserNotFoundException;

  Optional<PasswordResetToken> getToken(UserId id);

  void removeToken(PasswordResetToken token);

  class UserNotFoundException extends Exception {}
}
