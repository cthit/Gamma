package it.chalmers.gamma.app.port.repository;

import it.chalmers.gamma.app.domain.user.passwordreset.PasswordResetToken;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;

import java.util.Optional;

public interface PasswordResetRepository {

    PasswordResetToken createNewToken(User user);
    Optional<PasswordResetToken> getToken(UserId id);
    void removeToken(PasswordResetToken token);
}
