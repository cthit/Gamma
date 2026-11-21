package it.chalmers.gamma.app.signin;

import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.UserId;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Limits the wrong authenticator app codes entered for an account per day. Correct codes don't
 * count, so someone who only knows the password can't lock the owner out by using up the limit.
 */
@Service
public class TotpCodeLimit {

  private static final Logger LOGGER = LoggerFactory.getLogger(TotpCodeLimit.class);

  // A guess matches one of the three codes accepted at a time with odds of 3 in a million.
  public static final int MAX_WRONG_CODES_PER_DAY = 10;

  private final ThrottlingService throttling;
  private final ApplicationEventPublisher events;

  public TotpCodeLimit(ThrottlingService throttling, ApplicationEventPublisher events) {
    this.throttling = throttling;
    this.events = events;
  }

  /**
   * Starts checking a code, or returns empty if too many wrong codes have been entered. The attempt
   * is counted up front, so concurrent guesses can't get past the limit, and is taken back when
   * closed unless it was marked {@link Attempt#wrong()}.
   */
  public Optional<Attempt> start(UserId userId) {
    long number = this.throttling.count(key(userId));
    if (number > MAX_WRONG_CODES_PER_DAY) {
      this.throttling.uncount(key(userId));
      return Optional.empty();
    }
    return Optional.of(new Attempt(userId, number));
  }

  /** Called when the password changes or the account is recovered, which locks out the guesser. */
  public void reset(UserId userId) {
    try {
      this.throttling.reset(key(userId));
    } catch (RuntimeException e) {
      LOGGER.error("Could not reset the authenticator app code limit of user {}", userId.value());
    }
  }

  private static String key(UserId userId) {
    return "totp:" + userId.value();
  }

  public final class Attempt implements AutoCloseable {

    private final UserId userId;
    private final long number;
    private boolean wrong;

    private Attempt(UserId userId, long number) {
      this.userId = userId;
      this.number = number;
    }

    /** A wrong or already used code. */
    public void wrong() {
      this.wrong = true;
    }

    @Override
    public void close() {
      if (!this.wrong) {
        TotpCodeLimit.this.throttling.uncount(key(this.userId));
      } else if (this.number == MAX_WRONG_CODES_PER_DAY) {
        LOGGER.warn("User {} reached the limit of wrong authenticator codes", this.userId.value());
        TotpCodeLimit.this.events.publishEvent(new TotpCodesGuessed(this.userId));
      }
    }
  }
}
