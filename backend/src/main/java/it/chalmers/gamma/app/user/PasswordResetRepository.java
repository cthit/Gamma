package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.domain.PasswordResetToken;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserId;

import java.util.Optional;

public interface PasswordResetRepository {

    PasswordResetToken createNewToken(User user);
    Optional<PasswordResetToken> getToken(UserId id);
    void removeToken(PasswordResetToken token);
}
