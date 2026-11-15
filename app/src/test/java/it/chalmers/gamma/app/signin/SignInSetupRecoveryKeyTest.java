package it.chalmers.gamma.app.signin;

import static org.junit.jupiter.api.Assertions.*;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.signin.domain.PasskeyRepository;
import it.chalmers.gamma.app.signin.domain.RecoveryKey;
import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import it.chalmers.gamma.security.authentication.SignInSetupAuthentication.Reason;
import it.chalmers.gamma.security.signin.SignInSetupAuthenticationToken;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

class SignInSetupRecoveryKeyTest {

  private final UserId userId = new UserId(UUID.randomUUID());
  private final Instant revokedAt = Instant.now().minusSeconds(5);
  private boolean emailRecovery;
  private boolean hasPassword;
  private Instant keyCreatedAt;

  @AfterEach
  void clear() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void adminRecoveryRefusesAPasswordUntilANewRecoveryKeyIsSaved() throws Exception {
    SignInSetupFacade facade = setUp(Reason.ADMIN_RECOVERY);

    assertTrue(facade.getSetup().needsRecoveryKey());
    assertThrows(
        SignInSetupFacade.RecoveryKeyFirstException.class,
        () -> facade.setPassword("a-new-password-1", "a-new-password-1"));
    assertFalse(this.hasPassword);

    facade.saveRecoveryKey(RecoveryKey.generate());
    facade.setPassword("a-new-password-1", "a-new-password-1");
    assertTrue(this.hasPassword);
    assertNotNull(this.keyCreatedAt);
    assertFalse(facade.getSetup().needsRecoveryKey());
  }

  @Test
  void theUsedRecoveryKeyDoesNotCountWhenEmailRecoveryIsOff() throws Exception {
    Instant usedKey = this.revokedAt.minusSeconds(3600);
    this.keyCreatedAt = usedKey;
    SignInSetupFacade facade = setUp(Reason.RECOVERY_KEY);

    assertTrue(facade.getSetup().needsRecoveryKey());
    assertThrows(
        SignInSetupFacade.RecoveryKeyFirstException.class,
        () -> facade.setPassword("a-new-password-1", "a-new-password-1"));
    assertEquals(usedKey, this.keyCreatedAt, "The used key must work until it is replaced");
  }

  @Test
  void settingThePasswordAgainKeepsTheNewRecoveryKey() throws Exception {
    this.keyCreatedAt = this.revokedAt.minusSeconds(3600);
    SignInSetupFacade facade = setUp(Reason.RECOVERY_KEY);

    facade.saveRecoveryKey(RecoveryKey.generate());
    Instant newKey = this.keyCreatedAt;
    facade.setPassword("a-new-password-1", "a-new-password-1");
    facade.setPassword("a-new-password-2", "a-new-password-2");

    assertEquals(newKey, this.keyCreatedAt);
    assertFalse(facade.getSetup().needsRecoveryKey());
  }

  @Test
  void theUsedRecoveryKeyStopsWorkingOnceAPasswordIsSetWithEmailRecoveryOn() throws Exception {
    this.emailRecovery = true;
    this.keyCreatedAt = this.revokedAt.minusSeconds(3600);
    SignInSetupFacade facade = setUp(Reason.RECOVERY_KEY);

    assertFalse(facade.getSetup().needsRecoveryKey());
    facade.setPassword("a-new-password-1", "a-new-password-1");
    assertNull(this.keyCreatedAt);
  }

  @Test
  void anEmailSetupKeepsTheExistingRecoveryKey() throws Exception {
    this.emailRecovery = true;
    Instant existing = this.revokedAt.minusSeconds(3600);
    this.keyCreatedAt = existing;
    SignInSetupFacade facade = setUp(Reason.EMAIL);

    facade.setPassword("a-new-password-1", "a-new-password-1");
    assertEquals(existing, this.keyCreatedAt);
  }

  private SignInSetupFacade setUp(Reason reason) {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new SignInSetupAuthenticationToken(
                new SignInSetupAuthentication(
                    this.userId.value(), reason, this.revokedAt.plusMillis(1))));

    UserRepository users =
        stub(
            UserRepository.class,
            (name, args) ->
                switch (name) {
                  case "get" ->
                      Optional.of(
                          new GammaUser(
                              this.userId,
                              new Cid("tester"),
                              new Nick("Tester"),
                              new FirstName("Test"),
                              new LastName("User"),
                              new AcceptanceYear(2026),
                              null,
                              null));
                  case "lockSignInMethods" -> null;
                  case "isLocked" -> false;
                  case "sessionsRevokedAt" -> Optional.of(this.revokedAt);
                  case "isEmailRecoveryEnabled" -> this.emailRecovery;
                  case "hasPassword" -> this.hasPassword;
                  case "setPassword" -> {
                    this.hasPassword = true;
                    yield null;
                  }
                  default -> throw new AssertionError(name);
                });
    RecoveryKeyRepository keys =
        stub(
            RecoveryKeyRepository.class,
            (name, args) ->
                switch (name) {
                  case "createdAt" -> Optional.ofNullable(this.keyCreatedAt);
                  case "save" -> {
                    this.keyCreatedAt = Instant.now();
                    yield null;
                  }
                  case "delete" -> {
                    this.keyCreatedAt = null;
                    yield null;
                  }
                  default -> throw new AssertionError(name);
                });
    TotpRepository totps =
        stub(
            TotpRepository.class,
            (name, args) -> {
              if (!name.equals("isEnabled")) throw new AssertionError(name);
              return false;
            });
    PasskeyRepository passkeys =
        stub(
            PasskeyRepository.class,
            (name, args) -> {
              if (!name.equals("getAll")) throw new AssertionError(name);
              return List.of();
            });
    AdminRepository admins =
        stub(
            AdminRepository.class,
            (name, args) -> {
              if (!name.equals("isAdmin")) throw new AssertionError(name);
              return false;
            });

    return new SignInSetupFacade(
        new AccessGuard(users, null),
        users,
        admins,
        passkeys,
        totps,
        keys,
        new SignInSetupRecoveryKey(users, totps, keys));
  }

  private interface Handler {
    Object handle(String name, Object[] args);
  }

  @SuppressWarnings("unchecked")
  private static <T> T stub(Class<T> type, Handler handler) {
    return (T)
        Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class<?>[] {type},
            (proxy, method, args) -> handler.handle(method.getName(), args));
  }
}
