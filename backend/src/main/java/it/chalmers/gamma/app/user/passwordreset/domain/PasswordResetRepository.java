package it.chalmers.gamma.app.user.passwordreset.domain;

import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;

import java.util.Optional;

public interface PasswordResetRepository {

    PasswordResetToken createNewToken(User user);
    Optional<PasswordResetToken> getToken(UserId id);
    void removeToken(PasswordResetToken token);
}
