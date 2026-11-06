package it.chalmers.gamma.security;

import it.chalmers.gamma.app.client.domain.Scope;
import it.chalmers.gamma.app.oauth2.ClaimsMapper;
import it.chalmers.gamma.app.oauth2.GammaRegisteredClientRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationServerMetadata;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationServerMetadataClaimNames;
import org.springframework.security.oauth2.server.authorization.oidc.OidcProviderConfiguration;
import org.springframework.security.oauth2.server.authorization.oidc.OidcProviderMetadataClaimNames;

/**
 * Restricts the discovery documents ({@code /.well-known/oauth-authorization-server} and {@code
 * /.well-known/openid-configuration}) to what Gamma supports.
 *
 * <p>Spring Authorization Server advertises every feature it can provide. Gamma registers its
 * clients with a narrower set (see {@link GammaRegisteredClientRepository}). Gamma's token storage
 * also cannot look tokens up the way token revocation, token introspection and RP-initiated logout
 * need, so those endpoints only fail and are left out here.
 */
final class AuthorizationServerMetadataCustomizer {

  /** Claims that Spring adds to every ID token. */
  private static final List<String> ID_TOKEN_CLAIMS =
      List.of(
          IdTokenClaimNames.SUB,
          IdTokenClaimNames.ISS,
          IdTokenClaimNames.AUD,
          IdTokenClaimNames.EXP,
          IdTokenClaimNames.IAT,
          JwtClaimNames.JTI,
          IdTokenClaimNames.AUTH_TIME,
          IdTokenClaimNames.NONCE,
          IdTokenClaimNames.AZP,
          "sid");

  private AuthorizationServerMetadataCustomizer() {}

  static void customize(OAuth2AuthorizationServerMetadata.Builder builder) {
    builder.claims(AuthorizationServerMetadataCustomizer::customizeCommon);
  }

  static void customize(OidcProviderConfiguration.Builder builder) {
    builder.claims(
        claims -> {
          customizeCommon(claims);
          claims.remove(OidcProviderMetadataClaimNames.END_SESSION_ENDPOINT);
          claims.put(
              "claims_supported",
              Stream.concat(ID_TOKEN_CLAIMS.stream(), ClaimsMapper.USER_CLAIMS.stream()).toList());
        });
  }

  /** Applies to both the OAuth 2.0 authorization server metadata and the OpenID configuration. */
  private static void customizeCommon(Map<String, Object> claims) {
    claims.put(
        OAuth2AuthorizationServerMetadataClaimNames.GRANT_TYPES_SUPPORTED,
        GammaRegisteredClientRepository.GRANT_TYPES.stream()
            .map(AuthorizationGrantType::getValue)
            .toList());
    claims.put(
        OAuth2AuthorizationServerMetadataClaimNames.TOKEN_ENDPOINT_AUTH_METHODS_SUPPORTED,
        GammaRegisteredClientRepository.CLIENT_AUTHENTICATION_METHODS.stream()
            .map(ClientAuthenticationMethod::getValue)
            .toList());
    claims.put(
        OAuth2AuthorizationServerMetadataClaimNames.SCOPES_SUPPORTED,
        Stream.concat(
                Stream.of(OidcScopes.OPENID),
                Arrays.stream(Scope.values()).map(scope -> scope.name().toLowerCase()))
            .toList());

    claims.remove(OAuth2AuthorizationServerMetadataClaimNames.REVOCATION_ENDPOINT);
    claims.remove(
        OAuth2AuthorizationServerMetadataClaimNames.REVOCATION_ENDPOINT_AUTH_METHODS_SUPPORTED);
    claims.remove(OAuth2AuthorizationServerMetadataClaimNames.INTROSPECTION_ENDPOINT);
    claims.remove(
        OAuth2AuthorizationServerMetadataClaimNames.INTROSPECTION_ENDPOINT_AUTH_METHODS_SUPPORTED);
    // Gamma clients authenticate with a client secret, never with a TLS client certificate.
    claims.remove(
        OAuth2AuthorizationServerMetadataClaimNames.TLS_CLIENT_CERTIFICATE_BOUND_ACCESS_TOKENS);
    // The UserInfo endpoint does not accept DPoP-bound access tokens.
    claims.remove(OAuth2AuthorizationServerMetadataClaimNames.DPOP_SIGNING_ALG_VALUES_SUPPORTED);
  }
}
