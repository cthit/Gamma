package it.chalmers.gamma.app.user;

import it.chalmers.gamma.domain.PasswordResetToken;
import it.chalmers.gamma.domain.user.User;
import it.chalmers.gamma.domain.user.UserId;

import java.util.Optional;

public interface PasswordResetRepository {

    PasswordResetToken createNewToken(User user);
    Optional<PasswordResetToken> getToken(UserId id);
    void removeToken(PasswordResetToken token);
}
