package it.chalmers.gamma.app.oauth2;

import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.restriction.ClientRestriction;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.oauth2.domain.GammaAuthorizationRepository;
import it.chalmers.gamma.app.oauth2.domain.GammaAuthorizationToken;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserMembership;
import it.chalmers.gamma.security.signin.SignInAuthorizationManagers;
import java.security.Principal;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;

@Component
public class GammaAuthorizationService implements OAuth2AuthorizationService {

  private final GammaAuthorizationRepository gammaAuthorizationRepository;
  private final ClientRepository clientRepository;
  private final GroupRepository groupRepository;
  private final SignInAuthorizationManagers signIn;

  public GammaAuthorizationService(
      GammaAuthorizationRepository gammaAuthorizationRepository,
      ClientRepository clientRepository,
      GroupRepository groupRepository,
      SignInAuthorizationManagers signIn) {
    this.gammaAuthorizationRepository = gammaAuthorizationRepository;
    this.clientRepository = clientRepository;
    this.groupRepository = groupRepository;
    this.signIn = signIn;
  }

  @Override
  public void save(OAuth2Authorization authorization) {
    if (!isSignInValid(authorization)) {
      throw new OAuth2AuthenticationException(new OAuth2Error("invalid_grant"));
    }
    Authentication authentication = authorization.getAttribute(Principal.class.getName());

    if (authentication != null && authentication.isAuthenticated()) {
      Client client =
          this.clientRepository
              .get(ClientUid.valueOf(authorization.getRegisteredClientId()))
              .orElseThrow();

      // If the client has no restrictions, then any user can sign in.
      if (client.restrictions().isPresent()
          && !client.restrictions().get().superGroups().isEmpty()
          && !userPassesRestriction(
              client.restrictions().get(), UserId.valueOf(authentication.getName()))) {
        throw new UserNotAllowedRuntimeException();
      }
    }

    OAuth2Authorization authorizationToSave = authorization;
    if (authentication != null) {
      UsernamePasswordAuthenticationToken sanitizedAuthenticationToken =
          UsernamePasswordAuthenticationToken.authenticated(
              new User(authentication.getName(), "", authentication.getAuthorities()),
              null,
              authentication.getAuthorities());

      authorizationToSave =
          OAuth2Authorization.from(authorization)
              .attribute(Principal.class.getName(), sanitizedAuthenticationToken)
              .build();
    }

    gammaAuthorizationRepository.save(authorizationToSave);
  }

  private boolean userPassesRestriction(ClientRestriction restriction, UserId userId) {
    List<UserMembership> memberships = this.groupRepository.getAllByUser(userId);
    List<SuperGroupId> userSuperGroups =
        memberships.stream()
            .map(UserMembership::group)
            .map(group -> group.superGroup().id())
            .distinct()
            .toList();

    return restriction.superGroups().stream()
        .anyMatch(superGroup -> userSuperGroups.contains(superGroup.id()));
  }

  @Override
  public void remove(OAuth2Authorization authorization) {
    gammaAuthorizationRepository.remove(authorization);
  }

  @Override
  public OAuth2Authorization findById(String id) {
    return gammaAuthorizationRepository.findById(id).filter(this::isSignInValid).orElse(null);
  }

  @Override
  public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
    if (tokenType == null) {
      for (GammaAuthorizationToken.Type type : GammaAuthorizationToken.Type.values()) {
        OAuth2Authorization authorization =
            this.gammaAuthorizationRepository
                .findByToken(new GammaAuthorizationToken(token, type))
                .filter(this::isSignInValid)
                .orElse(null);
        if (authorization != null) {
          return authorization;
        }
      }
      return null;
    }
    return gammaAuthorizationRepository
        .findByToken(GammaAuthorizationToken.valueOf(token, tokenType))
        .filter(this::isSignInValid)
        .orElse(null);
  }

  private boolean isSignInValid(OAuth2Authorization authorization) {
    Authentication authentication = authorization.getAttribute(Principal.class.getName());
    if (authentication == null) {
      return false;
    }
    AuthorizationResult result = this.signIn.signedInUser().authorize(() -> authentication, null);
    return result != null && result.isGranted();
  }

  public static class UserNotAllowedRuntimeException extends RuntimeException {}
}
