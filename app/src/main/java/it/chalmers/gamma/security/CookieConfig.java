package it.chalmers.gamma.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.stereotype.Component;

/** All other cookie config can be found in application.yml */
@Component
public class CookieConfig {

  @Value("${application.cookie.validity-time}")
  private int validityTime;

  @Bean
  public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setSameSite("LAX");
    serializer.setUseSecureCookie(true);
    serializer.setCookieName("gamma");
    serializer.setDomainName("");
    serializer.setUseHttpOnlyCookie(true);
    serializer.setCookiePath("/");
    serializer.setCookieMaxAge(this.validityTime);
    return serializer;
  }
}
