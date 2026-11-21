package it.chalmers.gamma.app.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import it.chalmers.gamma.adapter.primary.web.DeleteYourAccountController;
import it.chalmers.gamma.adapter.primary.web.EmailChangeController;
import it.chalmers.gamma.app.admin.UsableAdminGuard;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.signin.domain.RecentSignIn;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.UserExtended;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.emailchange.UserEmailChangeFacade;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.ModelAndView;

class PasswordlessConfirmationExpiryTest {

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void passwordlessEmailChangeRedirectsToConfirmWithoutBurningThrottle() throws Exception {
    GammaUser user = sampleUser();
    signIn(user);

    UserRepository userRepository =
        proxy(
            UserRepository.class,
            (method, args) ->
                switch (method.getName()) {
                  case "lockSignInMethods" -> null;
                  case "isLocked", "hasPassword" -> false;
                  case "sessionsRevokedAt" -> Optional.empty();
                  default -> throw new UnsupportedOperationException(method.getName());
                });
    ThrottlingService throttlingService =
        new ThrottlingService(null) {
          @Override
          public boolean canProceed(String key, int maxAttemptsPerDay) {
            return fail("Throttle should not be consumed when confirmation expired");
          }
        };
    RecentSignIn expired =
        new RecentSignIn() {
          @Override
          public boolean isRecent() {
            return false;
          }

          @Override
          public boolean isCurrent() {
            return true;
          }
        };

    var facade =
        new UserEmailChangeFacade(
            new AccessGuard(null, null),
            userRepository,
            null,
            null,
            throttlingService,
            expired,
            "http://localhost");

    assertThrows(
        UserEmailChangeFacade.ConfirmationExpiredException.class,
        () -> facade.requestEmailChange("new@example.com", ""));

    var controller = new EmailChangeController(facade, null, null);
    var request = new MockHttpServletRequest("PUT", "/me/edit-email");
    request.addHeader("HX-Request", "true");
    var response = new MockHttpServletResponse();
    var form = new EmailChangeController.EditEmailForm("new@example.com", "");
    var binding = new BeanPropertyBindingResult(form, "form");

    assertNull(controller.editEmail(true, form, binding, request, response));
    assertEquals("/login/confirm", response.getHeader("HX-Redirect"));
  }

  @Test
  void passwordlessAccountDeletionRedirectsToConfirmWhenExpired() throws Exception {
    GammaUser user = sampleUser();
    signIn(user);

    UserRepository userRepository =
        proxy(
            UserRepository.class,
            (method, args) ->
                switch (method.getName()) {
                  case "lockSignInMethods" -> null;
                  case "hasPassword" -> false;
                  default -> throw new UnsupportedOperationException(method.getName());
                });
    UsableAdminGuard guard =
        new UsableAdminGuard(null) {
          @Override
          public Check lock() {
            return null;
          }
        };
    RecentSignIn expired =
        new RecentSignIn() {
          @Override
          public boolean isRecent() {
            return false;
          }

          @Override
          public boolean isCurrent() {
            return true;
          }
        };

    var facade =
        new MeFacade(
            new AccessGuard(null, null),
            userRepository,
            null,
            null,
            null,
            expired,
            null,
            null,
            null,
            null,
            guard,
            null);

    assertThrows(MeFacade.ConfirmationExpiredException.class, () -> facade.deleteMe("", false));

    var controller = new DeleteYourAccountController(facade);
    var request = new MockHttpServletRequest("DELETE", "/delete-your-account");
    var response = new MockHttpServletResponse();
    var form = new DeleteYourAccountController.DeleteYourAccountForm("", false);
    var binding = new BeanPropertyBindingResult(form, "form");

    assertNull(controller.deleteYourAccount(false, form, binding, request, response));
    assertEquals("/login/confirm", response.getRedirectedUrl());
  }

  @Test
  void nonHtmxInvalidDeleteFormRendersFullPage() throws Exception {
    GammaUser user = sampleUser();
    signIn(user);

    var facade =
        new MeFacade(
            new AccessGuard(null, null),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null) {
          @Override
          public void deleteMe(String password, boolean clientDataDeleted) {
            throw new IllegalArgumentException();
          }

          @Override
          public AccountDeletionOverviewDTO getAccountDeletionOverview() {
            return new AccountDeletionOverviewDTO(
                user.id().value(),
                user.cid().value(),
                user.firstName().value(),
                List.of(),
                false,
                List.of(),
                List.of());
          }
        };

    var controller = new DeleteYourAccountController(facade);
    var form = new DeleteYourAccountController.DeleteYourAccountForm("wrong", false);
    var binding = new BeanPropertyBindingResult(form, "form");
    ModelAndView mv =
        controller.deleteYourAccount(
            false,
            form,
            binding,
            new MockHttpServletRequest("DELETE", "/delete-your-account"),
            new MockHttpServletResponse());

    assertEquals("index", mv.getViewName());
    assertEquals("pages/delete-your-account", mv.getModel().get("page"));
  }

  private static GammaUser sampleUser() {
    return new GammaUser(
        new UserId(UUID.randomUUID()),
        new Cid("jhalpert"),
        new Nick("Jim"),
        new FirstName("Jim"),
        new LastName("Halpert"),
        new AcceptanceYear(2020),
        Language.EN,
        new UserExtended(new Email("jim@example.com"), 1, false, null));
  }

  private static void signIn(GammaUser user) {
    var token =
        UsernamePasswordAuthenticationToken.authenticated(
            user.id().value().toString(),
            null,
            List.of(
                FactorGrantedAuthority.withAuthority(FactorGrantedAuthority.WEBAUTHN_AUTHORITY)
                    .issuedAt(Instant.now().minusSeconds(900))
                    .build()));
    token.setDetails(new UserAuthentication(user, false));
    SecurityContextHolder.getContext().setAuthentication(token);
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
