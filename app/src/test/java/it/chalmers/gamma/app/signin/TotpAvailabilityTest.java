package it.chalmers.gamma.app.signin;

import static org.junit.jupiter.api.Assertions.assertThrows;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.signin.domain.RecentSignIn;
import it.chalmers.gamma.app.signin.domain.RecoveryKey;
import it.chalmers.gamma.app.signin.domain.TotpSecret;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class TotpAvailabilityTest {
  private final UserId userId = new UserId(UUID.randomUUID());

  @AfterEach
  void clearAuthentication() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void aUserWithoutAPasswordCanNotStartOrFinishAnAuthenticatorAppSetup() {
    signIn(false);
    SignInMethodsFacade facade = facade(false, false);
    assertThrows(SignInMethodsFacade.TotpUnavailableException.class, facade::startTotpSetup);
    assertThrows(
        SignInMethodsFacade.TotpUnavailableException.class,
        () -> facade.enableTotp(TotpSecret.generate(), 1, RecoveryKey.generate()));
  }

  @Test
  void anAdminCanNotSetUpAnAuthenticatorAppOrRecoveryKey() {
    signIn(true);
    SignInMethodsFacade facade = facade(true, true);
    assertThrows(SignInMethodsFacade.TotpUnavailableException.class, facade::startTotpSetup);
    assertThrows(
        SignInMethodsFacade.TotpUnavailableException.class,
        () -> facade.enableTotp(TotpSecret.generate(), 1, RecoveryKey.generate()));
    assertThrows(
        SignInMethodsFacade.RecoveryKeyUnavailableException.class,
        () -> facade.replaceRecoveryKey(RecoveryKey.generate()));
    assertThrows(
        SignInMethodsFacade.RecoveryKeyUnavailableException.class,
        () -> facade.disableEmailRecovery(RecoveryKey.generate()));
  }

  private SignInMethodsFacade facade(boolean admin, boolean hasPassword) {
    UserRepository users =
        (UserRepository)
            Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[] {UserRepository.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "lockSignInMethods" -> null;
                      case "hasPassword" -> hasPassword;
                      default ->
                          throw new AssertionError(
                              "Unexpected mutation or lookup: " + method.getName());
                    });
    AdminRepository admins =
        (AdminRepository)
            Proxy.newProxyInstance(
                AdminRepository.class.getClassLoader(),
                new Class<?>[] {AdminRepository.class},
                (proxy, method, args) -> {
                  if (!method.getName().equals("isAdmin"))
                    throw new AssertionError(method.getName());
                  return admin;
                });
    // Any credential mutation would reach a null collaborator and fail the test.
    return new SignInMethodsFacade(
        new AccessGuard(users, null),
        users,
        admins,
        null,
        null,
        null,
        new RecentSignIn() {
          @Override
          public boolean isRecent() {
            return true;
          }

          @Override
          public boolean isCurrent() {
            return true;
          }
        },
        null,
        null,
        null,
        "https://gamma.example.org");
  }

  private void signIn(boolean admin) {
    GammaUser user =
        new GammaUser(
            userId,
            new Cid("tester"),
            new Nick("Tester"),
            new FirstName("Test"),
            new LastName("User"),
            new AcceptanceYear(2026),
            null,
            null);
    SecurityContextHolder.getContext()
        .setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated(
                new UserAuthentication(user, admin), null, List.of()));
  }
}
