package it.chalmers.gamma.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.default-oauth2-client")
public record DefaultOAuth2Client(
    String clientName,
    String clientId,
    String clientSecret,
    String redirectUrl,
    String apiKey,
    String scopes) {}
