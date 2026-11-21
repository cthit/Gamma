package it.chalmers.gamma.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import it.chalmers.gamma.security.signin.SignInAuthorizationManagers;
import it.chalmers.gamma.security.signin.SignInSetupAuthenticationToken;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextHolder;

class UpdateUserPrincipalFilterTest {

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void clearsExpiredSetupSessionOnRecoveryLinkRequest() throws Exception {
    var filter = new UpdateUserPrincipalFilter(null, null, setupAllowed(false));
    var setup = setupToken();
    SecurityContextHolder.getContext().setAuthentication(setup);

    var request = new MockHttpServletRequest("GET", "/admin-recovery");
    request.setServletPath("/admin-recovery");
    var session = new MockHttpSession();
    request.setSession(session);

    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertTrue(session.isInvalid());
  }

  @Test
  void preservesExpiredSetupSessionOnSetupRouteSoExpiryBannerShows() throws Exception {
    var filter = new UpdateUserPrincipalFilter(null, null, setupAllowed(false));
    var setup = setupToken();
    SecurityContextHolder.getContext().setAuthentication(setup);

    var request = new MockHttpServletRequest("GET", "/sign-in-setup");
    request.setServletPath("/sign-in-setup");
    var session = new MockHttpSession();
    request.setSession(session);

    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertFalse(session.isInvalid());
  }

  @Test
  void keepsActiveSetupSessionIntact() throws Exception {
    var filter = new UpdateUserPrincipalFilter(null, null, setupAllowed(true));
    var setup = setupToken();
    SecurityContextHolder.getContext().setAuthentication(setup);

    var request = new MockHttpServletRequest("GET", "/admin-recovery");
    request.setServletPath("/admin-recovery");
    var session = new MockHttpSession();
    request.setSession(session);

    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertFalse(session.isInvalid());
  }

  private static SignInSetupAuthenticationToken setupToken() {
    return new SignInSetupAuthenticationToken(
        new SignInSetupAuthentication(
            UUID.randomUUID(), SignInSetupAuthentication.Reason.ADMIN_RECOVERY, Instant.now()));
  }

  private static SignInAuthorizationManagers setupAllowed(boolean allowed) {
    return new SignInAuthorizationManagers(null, null, null) {
      @Override
      public boolean isSetupStillAllowed(SignInSetupAuthenticationToken setup) {
        return allowed;
      }
    };
  }
}
