package it.chalmers.gamma.security;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.oauth2")
public record OAuth2SigningKeyProperties(String signingKey, List<String> verificationKeys) {

  public OAuth2SigningKeyProperties {
    verificationKeys =
        verificationKeys == null
            ? List.of()
            : verificationKeys.stream().filter(key -> key != null && !key.isBlank()).toList();
  }

  public boolean hasSigningKey() {
    return signingKey != null && !signingKey.isBlank();
  }
}
