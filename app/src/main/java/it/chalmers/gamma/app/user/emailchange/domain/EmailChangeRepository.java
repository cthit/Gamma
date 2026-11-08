package it.chalmers.gamma.app.user.emailchange.domain;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.UserId;
import java.time.Duration;
import java.util.Optional;

public interface EmailChangeRepository {

  /** How long a confirmation link stays valid. */
  Duration VALIDITY = Duration.ofHours(1);

  record EmailChange(UserId userId, Email newEmail) {}

  /**
   * Creates a new pending change for the user, replacing any earlier one, and returns the token for
   * the confirmation link. Only its hash is stored, so this is the only time it is available.
   */
  EmailChangeToken create(UserId userId, Email newEmail);

  /** Returns the pending change if the token is valid and belongs to the user. */
  Optional<EmailChange> get(EmailChangeToken token, UserId userId);

  /**
   * Removes the pending change if the token is valid and belongs to the user, and returns it. Each
   * token can only be used once.
   */
  Optional<EmailChange> use(EmailChangeToken token, UserId userId);

  int removeExpired();
}
