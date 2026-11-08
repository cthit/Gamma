package it.chalmers.gamma.security.signin;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.passkeys")
public record PasskeyProperties(String rpId, List<String> allowedOrigins) {

  public String rpIdOr(String baseUrl) {
    if (this.rpId != null && !this.rpId.isBlank()) {
      return this.rpId.trim();
    }
    return URI.create(baseUrl).getHost();
  }

  public Set<String> allowedOriginsOr(String baseUrl) {
    Set<String> origins =
        this.allowedOrigins == null
            ? Set.of()
            : this.allowedOrigins.stream()
                .filter(origin -> origin != null && !origin.isBlank())
                .map(String::trim)
                .collect(Collectors.toUnmodifiableSet());
    if (!origins.isEmpty()) {
      return origins;
    }

    URI uri = URI.create(baseUrl);
    return Set.of(uri.getScheme() + "://" + uri.getRawAuthority());
  }
}
