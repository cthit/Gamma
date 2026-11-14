package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.emailchange.domain.EmailChangeRepository;
import it.chalmers.gamma.app.user.emailchange.domain.EmailChangeToken;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserEmailChangeRepositoryAdapter implements EmailChangeRepository {

  private final UserEmailChangeJpaRepository repository;

  public UserEmailChangeRepositoryAdapter(UserEmailChangeJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public EmailChangeToken create(UserId userId, Email newEmail) {
    this.repository.deleteById(userId.value());
    this.repository.flush();

    EmailChangeToken token = EmailChangeToken.generate();

    UserEmailChangeEntity entity = new UserEmailChangeEntity();
    entity.userId = userId.value();
    entity.newEmail = newEmail.value();
    entity.tokenHash = token.hashed();

    this.repository.saveAndFlush(entity);

    return token;
  }

  @Override
  public Optional<EmailChange> get(EmailChangeToken token, UserId userId) {
    return this.repository
        .findValid(token.hashed(), userId.value(), cutoff())
        .map(UserEmailChangeEntity::toDomain);
  }

  @Override
  public Optional<EmailChange> use(EmailChangeToken token, UserId userId) {
    Instant cutoff = cutoff();
    Optional<EmailChange> change =
        this.repository
            .findValid(token.hashed(), userId.value(), cutoff)
            .map(UserEmailChangeEntity::toDomain);

    // Only the request that actually deletes the row may apply the change, so a link can't be
    // used twice even when it is opened twice at the same time.
    if (change.isEmpty()
        || this.repository.deleteValid(token.hashed(), userId.value(), cutoff) != 1) {
      return Optional.empty();
    }

    return change;
  }

  @Override
  public void delete(UserId userId) {
    this.repository.deleteById(userId.value());
  }

  @Override
  public int removeExpired() {
    return this.repository.deleteExpired(cutoff());
  }

  private static Instant cutoff() {
    return Instant.now().minus(VALIDITY);
  }
}
