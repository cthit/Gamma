package it.chalmers.gamma.adapter.secondary.jpa.user.password;

import it.chalmers.gamma.app.repository.PasswordResetRepository;
import it.chalmers.gamma.app.domain.user.passwordreset.PasswordResetToken;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
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
        userPasswordResetJpaRepository.save(new UserPasswordResetEntity(
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
