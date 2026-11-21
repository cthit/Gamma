package it.chalmers.gamma.security.signin;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

class HtmxAwareRedirectsTest {

  private final MockHttpSession session = new MockHttpSession();
  private final MockHttpServletResponse response = new MockHttpServletResponse();

  @Test
  void theCurrentPageOfAnHtmxRequestIsSavedWithoutHtmxHeaders() {
    for (RequestCache cache :
        List.of(HtmxAwareRedirects.requestCache(), new HttpSessionRequestCache())) {
      MockHttpServletRequest request = htmx("POST", "/me/edit-password");
      request.addHeader("hx-current-url", "https://gamma.test/me/security?tab=1");

      HtmxAwareRedirects.saveCurrentPageOfHtmxRequest(request, this.response, cache);

      SavedRequest saved = cache.getRequest(request, this.response);
      assertTrue(saved.getRedirectUrl().startsWith("https://gamma.test/me/security?tab=1"));
      assertEquals("GET", saved.getMethod());
      assertWithoutHtmxHeaders(saved);
    }
  }

  @Test
  void aBoostedPageLoadIsSavedWithoutHtmxHeaders() {
    RequestCache cache = HtmxAwareRedirects.requestCache();
    MockHttpServletRequest request = htmx("GET", "/my-clients");
    request.addHeader("hx-boosted", "true");

    cache.saveRequest(request, this.response);

    assertEquals(
        "https://gamma.test/my-clients", cache.getRequest(request, this.response).getRedirectUrl());
    assertWithoutHtmxHeaders(cache.getRequest(request, this.response));

    HttpServletRequest replayed = cache.getMatchingRequest(page("/my-clients"), this.response);
    assertNull(replayed.getHeader("HX-Request"));
    assertNull(replayed.getHeader("hx-boosted"));
    assertEquals("sv", replayed.getHeader("Accept-Language"));
  }

  @Test
  void htmxRequestsForFragmentsAreNotSaved() {
    RequestCache cache = HtmxAwareRedirects.requestCache();
    MockHttpServletRequest request = htmx("GET", "/me/edit-password");

    cache.saveRequest(request, this.response);

    assertNull(cache.getRequest(request, this.response));
  }

  @Test
  void theCurrentPageOfABoostedFormSubmitIsSavedWithoutHtmxHeaders() {
    RequestCache cache = HtmxAwareRedirects.requestCache();
    MockHttpServletRequest request = htmx("POST", "/me/security/totp");
    request.addHeader("hx-boosted", "true");
    request.addHeader("hx-current-url", "https://gamma.test/me/security/totp");

    cache.saveRequest(request, this.response);
    assertNull(cache.getRequest(request, this.response));

    HtmxAwareRedirects.saveCurrentPageOfHtmxRequest(request, this.response, cache);

    SavedRequest saved = cache.getRequest(request, this.response);
    assertEquals("https://gamma.test/me/security/totp", saved.getRedirectUrl());
    assertEquals("GET", saved.getMethod());
    assertWithoutHtmxHeaders(saved);
  }

  @Test
  void aBoostedPageLoadKeepsItsTargetUrlInsteadOfCurrentUrl() {
    RequestCache cache = HtmxAwareRedirects.requestCache();
    MockHttpServletRequest request = htmx("GET", "/my-clients");
    request.addHeader("hx-boosted", "true");
    request.addHeader("hx-current-url", "https://gamma.test/");

    cache.saveRequest(request, this.response);
    HtmxAwareRedirects.saveCurrentPageOfHtmxRequest(request, this.response, cache);

    assertEquals(
        "https://gamma.test/my-clients", cache.getRequest(request, this.response).getRedirectUrl());
  }

  private MockHttpServletRequest page(String path) {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
    request.setScheme("https");
    request.setServerName("gamma.test");
    request.setServerPort(443);
    request.setSession(this.session);
    request.addHeader("accept-language", "sv");
    return request;
  }

  private MockHttpServletRequest htmx(String method, String path) {
    MockHttpServletRequest request = page(path);
    request.setMethod(method);
    // HTTP/2 and most proxies send header names in lower case.
    request.addHeader("hx-request", "true");
    request.addHeader("hx-target", "main");
    return request;
  }

  private static void assertWithoutHtmxHeaders(SavedRequest saved) {
    assertTrue(
        saved.getHeaderNames().stream().noneMatch(name -> name.regionMatches(true, 0, "hx-", 0, 3)),
        saved.getHeaderNames().toString());
    assertTrue(saved.getHeaderValues("HX-Request").isEmpty());
    assertEquals(List.of("sv"), saved.getHeaderValues("Accept-Language"));
  }
}
