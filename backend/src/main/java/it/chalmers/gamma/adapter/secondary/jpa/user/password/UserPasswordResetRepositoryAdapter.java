package it.chalmers.gamma.adapter.secondary.jpa.user.password;

import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;

@Service("PasswordResetRepository")
@Transactional
public class UserPasswordResetRepositoryAdapter implements PasswordResetRepository {

    private final UserPasswordResetJpaRepository userPasswordResetJpaRepository;

    public UserPasswordResetRepositoryAdapter(UserPasswordResetJpaRepository userPasswordResetJpaRepository) {
        this.userPasswordResetJpaRepository = userPasswordResetJpaRepository;
    }

    @Override
    public PasswordResetToken createNewToken(User user) {
        PasswordResetToken token = PasswordResetToken.generate();
        userPasswordResetJpaRepository.saveAndFlush(new UserPasswordResetEntity(
                user.id().value(),
                Instant.now(),
                token.value())
        );
        return token;
    }

    @Override
    public Optional<PasswordResetToken> getToken(UserId id) {
        return userPasswordResetJpaRepository.findByUserId(id.value())
                .map(userPasswordResetEntity -> new PasswordResetToken(userPasswordResetEntity.getToken()));
    }

    @Override
    public void removeToken(PasswordResetToken token) {
        this.userPasswordResetJpaRepository.deleteByToken(token.value());
    }
}
