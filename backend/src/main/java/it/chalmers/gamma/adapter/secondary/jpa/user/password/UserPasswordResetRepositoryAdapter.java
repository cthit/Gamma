package it.chalmers.gamma.adapter.secondary.jpa.user.password;

import it.chalmers.gamma.app.user.PasswordResetRepository;
import it.chalmers.gamma.domain.PasswordResetToken;
import it.chalmers.gamma.domain.user.User;
import it.chalmers.gamma.domain.user.UserId;
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
