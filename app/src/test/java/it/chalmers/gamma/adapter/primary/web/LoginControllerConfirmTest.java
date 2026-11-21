package it.chalmers.gamma.adapter.primary.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.chalmers.gamma.app.signin.SignInMethodsFacade;
import it.chalmers.gamma.app.signin.domain.Passkey;
import it.chalmers.gamma.security.signin.SignInFactors;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

class LoginControllerConfirmTest {

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void upgradedSessionWithMergedPasswordFactorDoesNotShowPasskeyAddedBanner() {
    var token =
        UsernamePasswordAuthenticationToken.authenticated(
            "user-id",
            null,
            List.of(
                FactorGrantedAuthority.withAuthority(SignInFactors.PASSWORD)
                    .issuedAt(Instant.now().minusSeconds(1200))
                    .build(),
                FactorGrantedAuthority.withAuthority(SignInFactors.PASSKEY)
                    .issuedAt(Instant.now().minusSeconds(900))
                    .build()));
    SecurityContextHolder.getContext().setAuthentication(token);

    ModelAndView mv =
        new LoginController(passkeyAdminFacade())
            .getConfirm(null, null, null, false, new MockHttpServletResponse());

    assertEquals(false, mv.getModel().get("passkeyAdded"));
    assertEquals(false, mv.getModel().get("adminNeedsPasskey"));
  }

  @Test
  void passwordSessionWithoutPasskeyFactorShowsPasskeyAddedBannerAfterReplacement() {
    var token =
        UsernamePasswordAuthenticationToken.authenticated(
            "user-id",
            null,
            List.of(
                FactorGrantedAuthority.withAuthority(SignInFactors.PASSWORD)
                    .issuedAt(Instant.now())
                    .build()));
    SecurityContextHolder.getContext().setAuthentication(token);

    ModelAndView mv =
        new LoginController(passkeyAdminFacade())
            .getConfirm(null, null, null, false, new MockHttpServletResponse());

    assertEquals(true, mv.getModel().get("passkeyAdded"));
    assertEquals(true, mv.getModel().get("adminNeedsPasskey"));
  }

  private static SignInMethodsFacade passkeyAdminFacade() {
    return new SignInMethodsFacade(
        null, null, null, null, null, null, null, null, null, null, null, null, null) {
      @Override
      public SignInMethodsDTO getMine() {
        return new SignInMethodsDTO(
            false,
            List.of(new Passkey("cred-1", "Laptop", Instant.now(), Instant.now())),
            false,
            false,
            false,
            null,
            true);
      }
    };
  }
}
