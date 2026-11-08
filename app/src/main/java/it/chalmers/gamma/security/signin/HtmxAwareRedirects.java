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

/**
 * Redirects that also work for htmx requests. Htmx would follow a plain redirect and swap the page
 * it leads to into the part of the page it was updating, so htmx is told to load the whole page
 * instead.
 */
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

  /**
   * Only page loads are saved to be returned to after signing in: plain GET requests and htmx
   * boosted links. Saving an htmx request for a part of a page would return the user to only that
   * part.
   */
  public static HttpSessionRequestCache requestCache() {
    HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
    requestCache.setMatchingRequestParameterName(null);
    RequestMatcher pageLoads =
        request -> "GET".equals(request.getMethod()) && (!isHtmx(request) || isBoosted(request));
    requestCache.setRequestMatcher(pageLoads);
    return requestCache;
  }

  /**
   * For an htmx request for a part of a page, saves the page the user is on instead, so that the
   * user returns to it after signing in.
   */
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

    // Only the path is used, so the user can't be sent to another site.
    if (uri.getRawPath() == null || !uri.getRawPath().startsWith("/")) {
      return;
    }

    requestCache.saveRequest(new PageLoad(request, uri), response);
  }

  /** A plain GET request for the given page, as if the user had loaded it. */
  private static final class PageLoad extends HttpServletRequestWrapper {

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
    public String getHeader(String name) {
      return name.startsWith("HX-") ? null : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
      return name.startsWith("HX-") ? Collections.emptyEnumeration() : super.getHeaders(name);
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
