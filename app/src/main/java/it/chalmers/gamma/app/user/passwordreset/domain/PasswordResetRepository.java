package it.chalmers.gamma.app.user.passwordreset.domain;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.UserId;

public interface PasswordResetRepository {

  record PasswordReset(PasswordResetToken token, Email email) {}

  PasswordReset createNewToken(Email email) throws UserNotFoundException;

  PasswordReset createNewToken(Cid cid) throws UserNotFoundException;

  boolean isTokenValid(PasswordResetToken token);

  UserId useToken(PasswordResetToken token);

  class UserNotFoundException extends Exception {}

  class TokenNotFoundRuntimeException extends RuntimeException {}
}
