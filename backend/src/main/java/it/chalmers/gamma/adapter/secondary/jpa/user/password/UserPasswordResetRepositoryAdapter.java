package it.chalmers.gamma.adapter.secondary.jpa.user.password;

import it.chalmers.gamma.app.port.repository.PasswordResetRepository;
import it.chalmers.gamma.app.domain.user.passwordreset.PasswordResetToken;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("PasswordResetRepository")
public class UserPasswordResetRepositoryAdapter implements PasswordResetRepository {
    @Override
    public PasswordResetToken createNewToken(User user) {
        return null;
    }

    @Override
    public Optional<PasswordResetToken> getToken(UserId id) {
        return Optional.empty();
    }

    @Override
    public void removeToken(PasswordResetToken token) {

    }
}
