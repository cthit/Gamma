package it.chalmers.gamma.security.signin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.RequestMatcher;

public final class HtmxAwareRedirects {

  private HtmxAwareRedirects() {}

  public static boolean isHtmx(HttpServletRequest request) {
    return "true".equals(request.getHeader("HX-Request"));
  }

  private static boolean isBoosted(HttpServletRequest request) {
    return "true".equals(request.getHeader("HX-Boosted"));
  }

  public static void redirect(HttpServletRequest request, HttpServletResponse response, String url)
      throws IOException {
    if (isHtmx(request)) {
      response.setHeader("HX-Redirect", url);
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      response.sendRedirect(url);
    }
  }

  public static HttpSessionRequestCache requestCache() {
    RequestMatcher pageLoads =
        request -> "GET".equals(request.getMethod()) && (!isHtmx(request) || isBoosted(request));
    HttpSessionRequestCache requestCache =
        new HttpSessionRequestCache() {
          @Override
          public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
            // The saved headers are replayed, so a saved boosted request would get a fragment.
            if (pageLoads.matches(request)) {
              super.saveRequest(new WithoutHtmxHeaders(request), response);
            }
          }
        };
    requestCache.setMatchingRequestParameterName(null);
    return requestCache;
  }

  public static void saveCurrentPageOfHtmxRequest(
      HttpServletRequest request, HttpServletResponse response, RequestCache requestCache) {
    if (!isHtmx(request) || isBoosted(request)) {
      return;
    }

    String currentUrl = request.getHeader("HX-Current-URL");
    if (currentUrl == null) {
      return;
    }

    URI uri;
    try {
      uri = new URI(currentUrl);
    } catch (URISyntaxException e) {
      return;
    }

    if (uri.getRawPath() == null || !uri.getRawPath().startsWith("/")) {
      return;
    }

    requestCache.saveRequest(new PageLoad(request, uri), response);
  }

  private static class WithoutHtmxHeaders extends HttpServletRequestWrapper {

    private WithoutHtmxHeaders(HttpServletRequest request) {
      super(request);
    }

    private static boolean isHtmxHeader(String name) {
      return name.regionMatches(true, 0, "HX-", 0, 3);
    }

    @Override
    public String getHeader(String name) {
      return isHtmxHeader(name) ? null : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
      return isHtmxHeader(name) ? Collections.emptyEnumeration() : super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
      return Collections.enumeration(
          Collections.list(super.getHeaderNames()).stream()
              .filter(name -> !isHtmxHeader(name))
              .toList());
    }
  }

  private static final class PageLoad extends WithoutHtmxHeaders {

    private final URI uri;

    private PageLoad(HttpServletRequest request, URI uri) {
      super(request);
      this.uri = uri;
    }

    @Override
    public String getMethod() {
      return "GET";
    }

    @Override
    public String getRequestURI() {
      return this.uri.getRawPath();
    }

    @Override
    public String getServletPath() {
      return this.uri.getPath();
    }

    @Override
    public String getPathInfo() {
      return null;
    }

    @Override
    public String getQueryString() {
      return this.uri.getRawQuery();
    }

    @Override
    public StringBuffer getRequestURL() {
      StringBuffer url = new StringBuffer();
      url.append(getScheme()).append("://").append(getServerName());
      int port = getServerPort();
      if (!(("http".equals(getScheme()) && port == 80)
          || ("https".equals(getScheme()) && port == 443))) {
        url.append(':').append(port);
      }
      url.append(getRequestURI());
      return url;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
      return Map.of();
    }

    @Override
    public String getParameter(String name) {
      return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
      return Collections.emptyEnumeration();
    }

    @Override
    public String[] getParameterValues(String name) {
      return null;
    }
  }
}
