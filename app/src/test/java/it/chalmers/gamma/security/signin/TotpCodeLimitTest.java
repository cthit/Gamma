package it.chalmers.gamma.security.signin;

import static org.junit.jupiter.api.Assertions.*;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import it.chalmers.gamma.app.signin.TotpCodeLimit;
import it.chalmers.gamma.app.signin.TotpCodesGuessed;
import it.chalmers.gamma.app.signin.domain.Totp;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.signin.domain.TotpSecret;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.UserId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

class TotpCodeLimitTest {

  private static final int MAX = TotpCodeLimit.MAX_WRONG_CODES_PER_DAY;

  private final UserId userId = new UserId(UUID.randomUUID());
  private final TotpSecret secret = TotpSecret.generate();
  private final Map<String, Long> counts = new HashMap<>();
  private final List<Object> events = new ArrayList<>();
  private final TotpCodeLimit limit = new TotpCodeLimit(new CountingThrottling(), this.events::add);
  private final Totps totps = new Totps();
  private final TotpAuthenticationProvider provider =
      new TotpAuthenticationProvider(this.totps, this.limit);

  @Test
  void correctCodesDoNotUseUpTheLimit() throws Exception {
    for (int i = 0; i < MAX * 3; i++) {
      this.totps.lastUsedTimeStep = -1;
      assertDoesNotThrow(() -> verify(currentCode()));
    }
    assertEquals(0, count());

    for (int i = 0; i < MAX - 1; i++) {
      assertThrows(BadCredentialsException.class, () -> verify("000000"));
    }
    this.totps.lastUsedTimeStep = -1;
    assertDoesNotThrow(() -> verify(currentCode()));
    assertEquals(MAX - 1, count());
  }

  @Test
  void wrongAndReusedCodesReachTheLimitAndTellTheOwnerOnce() throws Exception {
    String code = currentCode();
    verify(code);
    for (int i = 0; i < MAX - 1; i++) {
      assertThrows(BadCredentialsException.class, () -> verify(code));
    }
    assertEquals(List.of(), this.events);
    assertThrows(BadCredentialsException.class, () -> verify("000000"));
    assertEquals(List.of(new TotpCodesGuessed(this.userId)), this.events);

    this.totps.lastUsedTimeStep = -1;
    String correct = currentCode();
    for (int i = 0; i < 3; i++) {
      assertThrows(TooManyTotpCodesException.class, () -> verify(correct));
    }
    assertEquals(MAX, count(), "A throttled attempt is not counted");
    assertEquals(-1, this.totps.lastUsedTimeStep, "A throttled code is not checked");
    assertEquals(1, this.events.size());

    this.limit.reset(this.userId);
    assertDoesNotThrow(() -> verify(correct));
  }

  private void verify(String code) {
    this.provider.authenticate(
        new TotpAuthenticationRequestToken(this.userId.value().toString(), code));
  }

  private long count() {
    return this.counts.getOrDefault("totp:" + this.userId.value(), 0L);
  }

  private String currentCode() throws Exception {
    TimeBasedOneTimePasswordGenerator generator = new TimeBasedOneTimePasswordGenerator();
    return generator.generateOneTimePasswordString(
        new SecretKeySpec(this.secret.bytes(), generator.getAlgorithm()), Instant.now());
  }

  private final class CountingThrottling extends ThrottlingService {

    CountingThrottling() {
      super(null);
    }

    @Override
    public long count(String key) {
      return TotpCodeLimitTest.this.counts.merge(key, 1L, Long::sum);
    }

    @Override
    public void uncount(String key) {
      TotpCodeLimitTest.this.counts.merge(key, -1L, Long::sum);
    }

    @Override
    public void reset(String key) {
      TotpCodeLimitTest.this.counts.remove(key);
    }
  }

  private final class Totps implements TotpRepository {

    private long lastUsedTimeStep = -1;

    @Override
    public Optional<Totp> get(UserId userId) {
      return Optional.of(new Totp(TotpCodeLimitTest.this.secret, this.lastUsedTimeStep));
    }

    @Override
    public boolean isEnabled(UserId userId) {
      return true;
    }

    @Override
    public void save(UserId userId, TotpSecret secret, long usedTimeStep) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean useTimeStep(UserId userId, TotpSecret secret, long timeStep) {
      if (timeStep <= this.lastUsedTimeStep) {
        return false;
      }
      this.lastUsedTimeStep = timeStep;
      return true;
    }

    @Override
    public void delete(UserId userId) {
      throw new UnsupportedOperationException();
    }
  }
}
