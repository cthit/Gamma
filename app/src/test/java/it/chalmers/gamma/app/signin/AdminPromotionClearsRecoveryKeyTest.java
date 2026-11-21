package it.chalmers.gamma.app.signin;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.chalmers.gamma.adapter.secondary.jpa.user.admin.AdminEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.admin.AdminEntityJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.admin.AdminEntityRepositoryAdapter;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class AdminPromotionClearsRecoveryKeyTest {

  private final UserId userId = new UserId(UUID.randomUUID());

  @Test
  void promotingAUserToAdminDeletesAnyExistingRecoveryKey() {
    AtomicBoolean savedAdmin = new AtomicBoolean();
    AtomicBoolean deletedRecoveryKey = new AtomicBoolean();

    AdminEntityJpaRepository adminJpa =
        stub(
            AdminEntityJpaRepository.class,
            (name, args) ->
                switch (name) {
                  case "findById" -> Optional.<AdminEntity>empty();
                  case "save" -> {
                    savedAdmin.set(true);
                    yield args[0];
                  }
                  default -> throw new AssertionError(name);
                });
    UserRepository users =
        stub(
            UserRepository.class,
            (name, args) -> {
              if ("lockSignInMethods".equals(name)) {
                return null;
              }
              throw new AssertionError(name);
            });
    RecoveryKeyRepository recoveryKeys =
        stub(
            RecoveryKeyRepository.class,
            (name, args) -> {
              if ("delete".equals(name) && this.userId.equals(args[0])) {
                deletedRecoveryKey.set(true);
                return null;
              }
              throw new AssertionError(name);
            });

    AdminEntityRepositoryAdapter adapter =
        new AdminEntityRepositoryAdapter(adminJpa, users, recoveryKeys);
    adapter.setAdmin(this.userId, true);

    assertTrue(savedAdmin.get());
    assertTrue(deletedRecoveryKey.get());
  }

  @Test
  void addingAnAdminPasskeyDeletesRecoveryKeyEvenWhenEmailRecoveryIsOff() {
    AtomicBoolean deletedRecoveryKey = new AtomicBoolean();

    UserRepository users =
        stub(
            UserRepository.class,
            (name, args) ->
                switch (name) {
                  case "hasPassword" -> false;
                  case "isEmailRecoveryEnabled" -> false;
                  default -> throw new AssertionError(name);
                });
    AdminRepository admins =
        stub(
            AdminRepository.class,
            (name, args) -> {
              if ("isAdmin".equals(name)) {
                return true;
              }
              throw new AssertionError(name);
            });
    TotpRepository totps =
        stub(
            TotpRepository.class,
            (name, args) -> {
              if ("delete".equals(name)) {
                return null;
              }
              throw new AssertionError(name);
            });
    RecoveryKeyRepository recoveryKeys =
        stub(
            RecoveryKeyRepository.class,
            (name, args) -> {
              if ("delete".equals(name) && this.userId.equals(args[0])) {
                deletedRecoveryKey.set(true);
                return null;
              }
              throw new AssertionError(name);
            });

    new PasskeyReplacesPassword(users, admins, totps, recoveryKeys, event -> {})
        .passkeyAdded(this.userId);

    assertTrue(deletedRecoveryKey.get());
  }

  @Test
  void addingANonAdminPasskeyKeepsRecoveryKeyWhenEmailRecoveryIsOff() {
    AtomicBoolean deletedRecoveryKey = new AtomicBoolean();

    UserRepository users =
        stub(
            UserRepository.class,
            (name, args) ->
                switch (name) {
                  case "hasPassword" -> false;
                  case "isEmailRecoveryEnabled" -> false;
                  default -> throw new AssertionError(name);
                });
    AdminRepository admins =
        stub(
            AdminRepository.class,
            (name, args) -> {
              if ("isAdmin".equals(name)) {
                return false;
              }
              throw new AssertionError(name);
            });
    TotpRepository totps =
        stub(
            TotpRepository.class,
            (name, args) -> {
              if ("delete".equals(name)) {
                return null;
              }
              throw new AssertionError(name);
            });
    RecoveryKeyRepository recoveryKeys =
        stub(
            RecoveryKeyRepository.class,
            (name, args) -> {
              if ("delete".equals(name)) {
                deletedRecoveryKey.set(true);
                return null;
              }
              throw new AssertionError(name);
            });

    new PasskeyReplacesPassword(users, admins, totps, recoveryKeys, event -> {})
        .passkeyAdded(this.userId);

    assertFalse(deletedRecoveryKey.get());
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
