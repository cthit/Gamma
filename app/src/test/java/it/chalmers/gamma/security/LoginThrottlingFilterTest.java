package it.chalmers.gamma.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.chalmers.gamma.app.throttling.ThrottlingService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class LoginThrottlingFilterTest {

  private final LoginThrottlingFilter filter =
      new LoginThrottlingFilter(
          new ThrottlingService(null) {
            @Override
            public boolean canProceed(String key, int maxPerDay) {
              return false;
            }
          });

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void anonymousSignInThrottlesBackToLogin() throws Exception {
    for (String path : List.of("/login", "/login/webauthn")) {
      MockHttpServletRequest request = new MockHttpServletRequest("POST", path);
      MockHttpServletResponse response = new MockHttpServletResponse();

      this.filter.doFilter(request, response, new MockFilterChain());

      assertEquals("/login?throttle=true", response.getRedirectedUrl());
    }
  }

  @Test
  void confirmationThrottlesBackToConfirmInsteadOfLogin() throws Exception {
    SecurityContextHolder.getContext()
        .setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated("user-id", null, List.of()));

    for (String path : List.of("/login", "/login/webauthn")) {
      MockHttpServletRequest request = new MockHttpServletRequest("POST", path);
      MockHttpServletResponse response = new MockHttpServletResponse();

      this.filter.doFilter(request, response, new MockFilterChain());

      assertEquals("/login/confirm?throttle", response.getRedirectedUrl());
    }
  }

  @Test
  void confirmingFormParameterThrottlesBackToConfirm() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
    request.setParameter("confirming", "true");
    MockHttpServletResponse response = new MockHttpServletResponse();

    this.filter.doFilter(request, response, new MockFilterChain());

    assertEquals("/login/confirm?throttle", response.getRedirectedUrl());
  }
}
