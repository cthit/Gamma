package it.chalmers.gamma.app.user.passwordreset;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.mail.domain.MailService;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetRepository;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PasswordResetThrottleTest {

  @Test
  void aThrottledRequestLeavesThePreviousLinkWorking() {
    UserId userId = new UserId(UUID.randomUUID());
    List<String> calls = new ArrayList<>();

    UserRepository users =
        stub(
            UserRepository.class,
            name ->
                switch (name) {
                  case "findId" -> Optional.of(userId);
                  case "isEmailRecoveryEnabled" -> true;
                  default -> throw new AssertionError(name);
                });
    AdminRepository admins = stub(AdminRepository.class, name -> false);
    PasswordResetRepository resets =
        stub(
            PasswordResetRepository.class,
            name -> {
              calls.add(name);
              throw new AssertionError("A new link replaced the previous one: " + name);
            });
    MailService mail =
        stub(
            MailService.class,
            name -> {
              throw new AssertionError("Mailed while throttled");
            });
    List<String> throttled = new ArrayList<>();
    ThrottlingService throttling =
        new ThrottlingService(null) {
          @Override
          public boolean canProceed(String key, int maxPerDay) {
            throttled.add(key);
            return false;
          }
        };

    new UserResetPasswordFacade(
            new AccessGuard(users, null),
            mail,
            users,
            resets,
            throttling,
            admins,
            null,
            null,
            null,
            "https://gamma.example.org")
        .startResetPasswordProcess("tester");

    assertEquals(List.of(userId.value() + "-password-reset"), throttled);
    assertEquals(List.of(), calls);
  }

  private interface Handler {
    Object handle(String name);
  }

  @SuppressWarnings("unchecked")
  private static <T> T stub(Class<T> type, Handler handler) {
    return (T)
        Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class<?>[] {type},
            (proxy, method, args) -> handler.handle(method.getName()));
  }
}
