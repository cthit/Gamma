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
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;

@Component
public class GammaAuthorizationService implements OAuth2AuthorizationService {

  private final Logger LOGGER = LoggerFactory.getLogger(GammaAuthorizationService.class);
  private final GammaAuthorizationRepository gammaAuthorizationRepository;
  private final ClientRepository clientRepository;
  private final GroupRepository groupRepository;

  public GammaAuthorizationService(
      GammaAuthorizationRepository gammaAuthorizationRepository,
      ClientRepository clientRepository,
      GroupRepository groupRepository) {
    this.gammaAuthorizationRepository = gammaAuthorizationRepository;
    this.clientRepository = clientRepository;
    this.groupRepository = groupRepository;
  }

  @Override
  public void save(OAuth2Authorization authorization) {
    UsernamePasswordAuthenticationToken authenticationToken =
        authorization.getAttribute("java.security.Principal");

    if (authenticationToken != null && authenticationToken.getPrincipal() instanceof User user) {
      Client client =
          this.clientRepository
              .get(ClientUid.valueOf(authorization.getRegisteredClientId()))
              .orElseThrow();

      // If the client has no restrictions, then any user can sign in.
      if (client.restrictions().isPresent()
          && !userPassesRestriction(client.restrictions().get(), user)) {
        throw new UserNotAllowedRuntimeException();
      }
    }

    gammaAuthorizationRepository.save(authorization);
  }

  private boolean userPassesRestriction(ClientRestriction restriction, User user) {
    UserId userId = UserId.valueOf(user.getUsername());

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
    return gammaAuthorizationRepository.findById(id).orElseThrow();
  }

  @Override
  public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
    return gammaAuthorizationRepository
        .findByToken(GammaAuthorizationToken.valueOf(token, tokenType))
        .orElseThrow();
  }

  public static class UserNotAllowedRuntimeException extends RuntimeException {}
}
