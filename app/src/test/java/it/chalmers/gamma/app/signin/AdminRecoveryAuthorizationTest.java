package it.chalmers.gamma.app.signin;

import static org.junit.jupiter.api.Assertions.assertThrows;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.signin.domain.RecentSignIn;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class AdminRecoveryAuthorizationTest {
  private final UserId actorId = new UserId(UUID.randomUUID());
  private final UUID targetId = UUID.randomUUID();

  @AfterEach
  void clearAuthentication() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void anAdminSessionWithoutRecentConfirmationCannotResetCredentialsOrIssueALink() {
    signIn(true);
    SignInMethodsFacade facade = facade(false, false);
    assertThrows(
        AccessGuard.AccessDeniedException.class, () -> facade.resetSignInMethods(targetId));
  }

  @Test
  void anAdminTargetMustBeDemotedBeforeRecoveryEvenWithRecentConfirmation() {
    signIn(true);
    SignInMethodsFacade facade = facade(true, true);
    assertThrows(
        SignInMethodsFacade.AdminRecoveryRequiresDemotionException.class,
        () -> facade.resetSignInMethods(targetId));
  }

  @Test
  void emailRecoveryCannotBeEnabledBeforeVerifyingAnAddress() {
    signIn(false);
    SignInMethodsFacade facade = facade(true, false);
    assertThrows(
        SignInMethodsFacade.RecoveryEmailUnverifiedException.class, facade::enableEmailRecovery);
  }

  private SignInMethodsFacade facade(boolean recent, boolean targetAdmin) {
    UserRepository users =
        (UserRepository)
            Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[] {UserRepository.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "lockSignInMethods" -> null;
                      case "emailRecoveryRequiresVerification" -> true;
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
                  return actorId.equals(args[0]) || targetAdmin;
                });
    // Any credential mutation or token creation would reach a null collaborator and fail the test.
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
            return recent;
          }

          @Override
          public boolean isCurrent() {
            throw new AssertionError("Recovery must require recent confirmation");
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
            actorId,
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
