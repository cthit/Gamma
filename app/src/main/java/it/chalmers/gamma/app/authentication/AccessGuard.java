package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.client.domain.*;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.LocalRunnerAuthentication;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccessGuard {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccessGuard.class);

  private final UserRepository userRepository;
  private final ClientRepository clientRepository;

  public AccessGuard(UserRepository userRepository, ClientRepository clientRepository) {
    this.userRepository = userRepository;
    this.clientRepository = clientRepository;
  }

  public static AccessChecker isAdmin() {
    return (clientRepository, userRepository) -> {
      if (AuthenticationExtractor.getAuthentication()
          instanceof UserAuthentication userAuthenticated) {
        return userAuthenticated.isAdmin();
      }

      return false;
    };
  }

  public static AccessChecker passwordCheck(String password) {
    return (clientRepository, userRepository) -> {
      if (AuthenticationExtractor.getAuthentication()
          instanceof UserAuthentication userAuthenticated) {
        GammaUser user = userAuthenticated.gammaUser();
        return userRepository.checkPassword(user.id(), new UnencryptedPassword(password));
      }

      return false;
    };
  }

  public static AccessChecker isApi(ApiKeyType apiKeyType) {
    return (clientRepository, userRepository) -> {
      if (AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiPrincipal) {
        return apiPrincipal.get().keyType() == apiKeyType;
      }

      return false;
    };
  }

  public static AccessChecker isClientApi() {
    return (clientRepository, userRepository) -> {
      if (AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiPrincipal) {
        return apiPrincipal.get().keyType() == ApiKeyType.CLIENT;
      }

      return false;
    };
  }

  public static AccessChecker isSpecificApi(ApiKeyId apiKeyId) {
    return (clientRepository, userRepository) -> {
      if (AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiPrincipal) {
        return apiPrincipal.get().id().equals(apiKeyId);
      }

      return false;
    };
  }

  public static AccessChecker isSignedInUserMemberOfGroup(Group group) {
    return (clientRepository, userRepository) -> {
      if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication userPrincipal) {
        GammaUser user = userPrincipal.gammaUser();
        return group.groupMembers().stream()
            .anyMatch(groupMember -> groupMember.user().equals(user));
      }

      return false;
    };
  }

  public static AccessChecker isSignedIn() {
    return (clientRepository, userRepository) ->
        AuthenticationExtractor.getAuthentication() instanceof UserAuthentication;
  }

  public static AccessChecker isNotSignedIn() {
    return (clientRepository, userRepository) ->
        AuthenticationExtractor.getAuthentication() == null;
  }

  public static AccessChecker userHasAcceptedClient(UserId id) {
    return (clientRepository, userRepository) -> {
      if (AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiPrincipal) {
        if (apiPrincipal.getClient().isPresent()) {
          Client client = apiPrincipal.getClient().get();
          return clientRepository.isApprovedByUser(id, client.clientUid());
        }
      }

      return false;
    };
  }

  public static AccessChecker ownerOfClient(ClientUid clientUid) {
    return (clientRepository, userRepository) -> {
      if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication userPrincipal) {
        Optional<Client> client = clientRepository.get(clientUid);

        if (client.isPresent()) {
          ClientOwner clientOwner = client.get().owner();

          if (clientOwner instanceof ClientUserOwner(UserId userId)) {
            return userId.equals(userPrincipal.gammaUser().id());
          }
        }
      }

      return false;
    };
  }

  /** Such as Bootstrap */
  public static AccessChecker isLocalRunner() {
    return (clientRepository, userRepository) ->
        AuthenticationExtractor.getAuthentication() instanceof LocalRunnerAuthentication;
  }

  public void require(AccessChecker check) {
    if (!validate(check)) {
      throw new AccessDeniedException();
    }
  }

  public void requireEither(AccessChecker... checks) {
    for (AccessChecker check : checks) {
      if (validate(check)) {
        return;
      }
    }

    // None of the check went through thus access denied
    throw new AccessDeniedException();
  }

  public void requireAll(AccessChecker... checks) {
    for (AccessChecker check : checks) {
      if (!validate(check)) {
        // If any of the checks fails, then access denied
        throw new AccessDeniedException();
      }
    }
  }

  private boolean validate(AccessChecker check) {
    return check.validate(clientRepository, userRepository);
  }

  public interface AccessChecker {
    boolean validate(ClientRepository clientRepository, UserRepository userRepository);
  }

  public static class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
      LOGGER.error("Access was denied.");
    }
  }
}
