package it.chalmers.gamma.app.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.signin.domain.RecentSignIn;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.app.user.emailchange.domain.EmailChangeRepository;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class AdminEmailChangeTest {
  private final UserId actorId = new UserId(UUID.randomUUID());
  private final UserId targetId = new UserId(UUID.randomUUID());
  private final List<String> calls = new ArrayList<>();
  private final List<Object> events = new ArrayList<>();

  @BeforeEach
  void signInAsAdmin() {
    SecurityContextHolder.getContext()
        .setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated(
                new UserAuthentication(user(actorId, "admin@example.org"), true), null, List.of()));
  }

  @AfterEach
  void clearAuthentication() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void changingTheEmailWithoutRecentConfirmationIsDeniedBeforeAnythingIsSaved() {
    UserFacade facade = facade(false);
    assertThrows(
        AccessGuard.AccessDeniedException.class,
        () -> facade.updateUser(update("someone-else@example.org")));
    assertEquals(List.of("lockSignInMethods", "get"), this.calls);
    assertTrue(this.events.isEmpty());
  }

  @Test
  void changingTheEmailRequiresVerificationAndDropsPendingEmailChanges() {
    facade(true).updateUser(update("someone-else@example.org"));
    assertEquals(
        List.of(
            "lockSignInMethods",
            "get",
            "save",
            "requireEmailRecoveryVerification",
            "emailChange.delete"),
        this.calls);
    assertEquals(
        List.of(new EmailChangedByAdmin(this.targetId, new Email("target@example.org"))),
        this.events);
  }

  @Test
  void keepingTheEmailLeavesRecoveryAloneEvenWithoutRecentConfirmation() {
    facade(false).updateUser(update("Target@Example.org"));
    assertEquals(List.of("lockSignInMethods", "get", "save"), this.calls);
    assertTrue(this.events.isEmpty());
  }

  private UserFacade.UpdateUser update(String email) {
    return new UserFacade.UpdateUser(
        this.targetId.value(), "Target", "Tar", "Get", email, "EN", 2026);
  }

  private UserFacade facade(boolean recent) {
    UserRepository users =
        (UserRepository)
            Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[] {UserRepository.class},
                (proxy, method, args) -> {
                  String name = method.getName();
                  if (!List.of(
                          "lockSignInMethods", "get", "save", "requireEmailRecoveryVerification")
                      .contains(name)) {
                    throw new AssertionError("Unexpected call: " + name);
                  }
                  if (!name.equals("save")) {
                    assertEquals(this.targetId, args[0]);
                  }
                  this.calls.add(name);
                  return name.equals("get")
                      ? Optional.of(user(this.targetId, "target@example.org"))
                      : null;
                });
    EmailChangeRepository emailChanges =
        (EmailChangeRepository)
            Proxy.newProxyInstance(
                EmailChangeRepository.class.getClassLoader(),
                new Class<?>[] {EmailChangeRepository.class},
                (proxy, method, args) -> {
                  if (!method.getName().equals("delete")) {
                    throw new AssertionError("Unexpected call: " + method.getName());
                  }
                  assertEquals(this.targetId, args[0]);
                  this.calls.add("emailChange.delete");
                  return null;
                });
    return new UserFacade(
        new AccessGuard(users, null),
        users,
        null,
        null,
        null,
        emailChanges,
        new RecentSignIn() {
          @Override
          public boolean isRecent() {
            return recent;
          }

          @Override
          public boolean isCurrent() {
            throw new AssertionError("An email change must require recent confirmation");
          }
        },
        this.events::add);
  }

  private static GammaUser user(UserId id, String email) {
    return new GammaUser(
        id,
        new Cid("tester"),
        new Nick("Tester"),
        new FirstName("Test"),
        new LastName("User"),
        new AcceptanceYear(2026),
        Language.EN,
        new UserExtended(new Email(email), 0, false, null));
  }
}
