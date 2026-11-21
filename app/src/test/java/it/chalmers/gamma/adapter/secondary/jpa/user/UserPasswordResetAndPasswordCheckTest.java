package it.chalmers.gamma.adapter.secondary.jpa.user;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserPasswordResetAndPasswordCheckTest {

  @Test
  void recoveryTokenCreatedAtIsAlwaysAfterSessionsRevokedAt() throws Exception {
    UUID id = UUID.randomUUID();
    UserEntity user = new UserEntity();
    user.id = id;
    user.email = "user@example.com";
    user.locked = false;
    user.emailRecovery = true;
    user.emailRecoveryRequiresVerification = false;
    user.sessionsRevokedAt = Instant.now().plusSeconds(5);

    AtomicReference<UserPasswordResetEntity> saved = new AtomicReference<>();

    UserJpaRepository userJpaRepository =
        proxy(
            UserJpaRepository.class,
            (method, args) ->
                switch (method.getName()) {
                  case "findById" -> Optional.of(user);
                  default -> throw new UnsupportedOperationException(method.getName());
                });
    UserPasswordResetJpaRepository resetJpaRepository =
        proxy(
            UserPasswordResetJpaRepository.class,
            (method, args) ->
                switch (method.getName()) {
                  case "deleteById", "flush" -> null;
                  case "save" -> {
                    saved.set((UserPasswordResetEntity) args[0]);
                    yield args[0];
                  }
                  default -> throw new UnsupportedOperationException(method.getName());
                });
    AdminRepository adminRepository =
        proxy(
            AdminRepository.class,
            (method, args) ->
                switch (method.getName()) {
                  case "isAdmin" -> false;
                  default -> throw new UnsupportedOperationException(method.getName());
                });
    UserRepository userRepository =
        proxy(
            UserRepository.class,
            (method, args) ->
                switch (method.getName()) {
                  case "lockSignInMethods" -> null;
                  default -> throw new UnsupportedOperationException(method.getName());
                });

    var adapter =
        new UserPasswordResetRepositoryAdapter(
            userJpaRepository, resetJpaRepository, adminRepository, userRepository);

    adapter.createAdminRecoveryToken(new UserId(id));

    UserPasswordResetEntity entity = saved.get();
    assertNotNull(entity);
    assertTrue(entity.toDomain().createdAt().isAfter(user.sessionsRevokedAt));
    assertTrue(adapter.isStillValid(entity));
  }

  @Test
  void checkPasswordReturnsFalseWhenUserHasNoPassword() throws Exception {
    UUID id = UUID.randomUUID();
    UserEntity user = new UserEntity();
    user.id = id;
    user.password = null;

    UserJpaRepository userJpaRepository =
        proxy(
            UserJpaRepository.class,
            (method, args) ->
                switch (method.getName()) {
                  case "findById" -> Optional.of(user);
                  default -> throw new UnsupportedOperationException(method.getName());
                });
    PasswordEncoder passwordEncoder =
        new PasswordEncoder() {
          @Override
          public String encode(CharSequence rawPassword) {
            return fail("encode should not be called");
          }

          @Override
          public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return fail("matches should not be called when password is null");
          }
        };

    var adapter =
        new UserRepositoryAdapter(
            userJpaRepository, null, null, passwordEncoder, null, null, null, null);

    assertFalse(adapter.checkPassword(new UserId(id), new UnencryptedPassword("password1337")));
  }

  @FunctionalInterface
  private interface Handler {
    Object handle(java.lang.reflect.Method method, Object[] args);
  }

  @SuppressWarnings("unchecked")
  private static <T> T proxy(Class<T> type, Handler handler) {
    return (T)
        Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class<?>[] {type},
            (proxy, method, args) -> handler.handle(method, args));
  }
}
