package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.bootstrap.BootstrapAuthenticated;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

/*
 * One could think that using AuthenticatedService would be a great idea.
 * The problem there is that there will be a circular dependency.
 */
@Service
public class UserAccessGuard {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserAccessGuard.class);

  private final ClientRepository clientRepository;

  public UserAccessGuard(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public boolean accessToExtended(UserId userId) {
    return isMe(userId)
        || isAdmin()
        || isLocalRunnerAuthenticated()
        || isApiKeyWithExtendedAccess()
        || isClientWithEmailScope();
  }

  public boolean isMe(UserId userId) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
      return UserId.valueOf(usernamePasswordAuthenticationToken.getName()).equals(userId);
    }

    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      return UserId.valueOf(jwtAuthenticationToken.getName()).equals(userId);
    }

    return false;
  }

  public boolean isAdmin() {
    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication authenticationDetails) {
      return authenticationDetails.isAdmin();
    }

    return false;
  }

  public boolean haveAccessToUser(UserId userId, boolean userLocked) {
    if (SecurityContextHolder.getContext().getAuthentication() == null
        || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
      return false;
    }

    // Always access to yourself
    if (isMe(userId)) {
      return true;
    }

    /*
     * If the user is locked then nothing should be returned
     * unless if and only if the signed-in user is an admin,
     * or if we are using an API with extended access.
     */
    if (userLocked) {
      return isAdmin() || isApiKeyWithExtendedAccess();
    }

    // If one user is trying to access another user, then approve
    if (isInternalAuthenticated()) {
      return true;
    }

    // If a client is trying to access a user that have approved the client, then approve
    if (haveAcceptedClient(userId)) {
      return true;
    }

    if (isApiKeyWithAccess()) {
      return true;
    }

    // If it's a local runner, then approve
    if (isLocalRunnerAuthenticated()) {
      return true;
    }

    LOGGER.info("tried to access the user: {}; ", userId);

    // Return false by default
    return false;
  }

  private boolean isInternalAuthenticated() {
    return AuthenticationExtractor.getAuthentication() instanceof UserAuthentication;
  }

  private boolean isLocalRunnerAuthenticated() {
    return SecurityContextHolder.getContext().getAuthentication() instanceof BootstrapAuthenticated;
  }

  // If the client tries to access a user that have not accepted the client, then return null.
  private boolean haveAcceptedClient(UserId userId) {
    if (AuthenticationExtractor.getAuthentication()
        instanceof ApiAuthentication apiAuthenticationPrincipal) {
      ApiKeyType apiKeyType = apiAuthenticationPrincipal.get().keyType();
      if (apiKeyType.equals(ApiKeyType.CLIENT)) {
        if (apiAuthenticationPrincipal.getClient().isEmpty()) {
          throw new IllegalStateException(
              "An api key that is of type CLIENT must have a client connected to them; "
                  + apiAuthenticationPrincipal.get());
        }

        return clientRepository.isApprovedByUser(
            userId, apiAuthenticationPrincipal.getClient().get().clientUid());
      }
    }

    if (SecurityContextHolder.getContext().getAuthentication()
      instanceof OAuth2ClientAuthenticationToken token) {
      var client = token.getRegisteredClient();

      if (client == null) {
        return false;
      }

      var clientUid = ClientUid.valueOf(client.getId());

      return clientRepository.isApprovedByUser(userId, clientUid);
    }

    return false;
  }

  /** Api Key with type INFO or ACCOUNT_SCAFFOLD have access to user information. */
  private boolean isApiKeyWithAccess() {
    if (AuthenticationExtractor.getAuthentication()
        instanceof ApiAuthentication apiAuthenticationPrincipal) {
      ApiKeyType apiKeyType = apiAuthenticationPrincipal.get().keyType();
      return apiKeyType.equals(ApiKeyType.INFO) || apiKeyType.equals(ApiKeyType.ACCOUNT_SCAFFOLD);
    }

    return false;
  }

  private boolean isApiKeyWithExtendedAccess() {
    if (AuthenticationExtractor.getAuthentication()
        instanceof ApiAuthentication apiAuthenticationPrincipal) {
      ApiKeyType apiKeyType = apiAuthenticationPrincipal.get().keyType();
      return apiKeyType.equals(ApiKeyType.ACCOUNT_SCAFFOLD);
    }

    return false;
  }

  private boolean isClientWithEmailScope() {
    if (SecurityContextHolder.getContext().getAuthentication()
            instanceof OAuth2ClientAuthenticationToken client
        && client.getRegisteredClient() != null) {
      return client.getRegisteredClient().getScopes().contains("email");
    }

    return false;
  }
}
