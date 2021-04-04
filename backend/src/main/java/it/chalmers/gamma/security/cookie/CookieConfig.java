package it.chalmers.gamma.security.cookie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.stereotype.Component;

/**
 * All other cookie config can be found in application.yml and application-production.yml
 */
@Component
public class CookieConfig {

    @Value("${application.production}")
    private boolean production;

    @Value("${application.cookie.domain}")
    private String domain;

    @Value("${application.cookie.path}")
    private String path;

    @Value("${application.cookie.validity-time}")
    private int validityTime;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("STRICT");
        serializer.setUseSecureCookie(this.production);
        serializer.setCookieName("gamma");
        serializer.setDomainName(this.domain);
        serializer.setUseHttpOnlyCookie(true);
        serializer.setCookiePath(this.path);
        serializer.setCookieMaxAge(this.validityTime);
        return serializer;
    }

    @Bean
    public CookieCsrfTokenRepository cookieCsrfTokenRepository() {
        CookieCsrfTokenRepository repo = new CookieCsrfTokenRepository();
        repo.setCookieDomain(this.domain);
        repo.setCookiePath(this.path);
        repo.setSecure(this.production);
        repo.setCookieHttpOnly(false);
        return repo;
    }

}
