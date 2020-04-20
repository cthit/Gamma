package it.chalmers.gamma.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

    //Remember Me functionality is in WebSecurityConfig
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("STRICT");
        serializer.setUseSecureCookie(this.production);
        serializer.setCookieName("gamma");
        serializer.setDomainName(this.domain);
        serializer.setUseHttpOnlyCookie(true);
        serializer.setCookiePath(this.path);
        serializer.setCookieMaxAge(validityTime);
        return serializer;
    }

}
