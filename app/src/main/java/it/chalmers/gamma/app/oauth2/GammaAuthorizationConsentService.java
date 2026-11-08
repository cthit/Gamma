package it.chalmers.gamma.app.oauth2;

import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.ClientUserOwner;
import it.chalmers.gamma.app.datadeletion.DataDeletionRecorder;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class GammaAuthorizationConsentService implements OAuth2AuthorizationConsentService {

  private final Logger LOGGER = LoggerFactory.getLogger(GammaAuthorizationConsentService.class);
  private final ClientRepository clientRepository;
  private final DataDeletionRecorder dataDeletionRecorder;
  private final UserRepository userRepository;

  public GammaAuthorizationConsentService(
      ClientRepository clientRepository,
      DataDeletionRecorder dataDeletionRecorder,
      UserRepository userRepository) {
    this.clientRepository = clientRepository;
    this.dataDeletionRecorder = dataDeletionRecorder;
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public void save(OAuth2AuthorizationConsent authorizationConsent) {
    Client client =
        this.clientRepository
            .get(ClientUid.valueOf(authorizationConsent.getRegisteredClientId()))
            .orElseThrow();

    List<String> consentedScopes =
        authorizationConsent.getScopes().stream().map(String::toLowerCase).sorted().toList();

    List<String> clientScopes =
        new ArrayList<>(client.scopes().stream().map(scope -> scope.name().toLowerCase()).toList());
    clientScopes.add("openid");
    clientScopes.sort(String::compareTo);

    if (consentedScopes.size() != clientScopes.size() || !consentedScopes.equals(clientScopes)) {
      throw new IllegalStateException(
          "Must have the same scopes for the authorize request and what is on the client."
              + "Consent scopes: "
              + consentedScopes
              + "Client scopes: "
              + clientScopes);
    }

    if (client.owner() instanceof ClientUserOwner
        && this.clientRepository.isUserOwnerLocked(client.clientUid())) {
      throw new OAuth2AuthorizationCodeRequestAuthenticationException(
          new OAuth2Error(
              OAuth2ErrorCodes.ACCESS_DENIED, "The owner of the client is locked", null),
          null);
    }

    UserId userId = UserId.valueOf(authorizationConsent.getPrincipalName());
    this.userRepository.lockForUpdate(userId);
    this.clientRepository.addClientApproval(userId, client.clientUid());
    this.dataDeletionRecorder.recordReApproval(userId, client.clientUid());
  }

  @Override
  public void remove(OAuth2AuthorizationConsent authorizationConsent) {
    // Use instead MeFacade.deleteUserApproval
    throw new UnsupportedOperationException();
  }

  @Override
  public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
    if (this.clientRepository.isApprovedByUser(
        UserId.valueOf(principalName), ClientUid.valueOf(registeredClientId))) {
      Optional<Client> maybeClient =
          this.clientRepository.get(ClientUid.valueOf(registeredClientId));

      if (maybeClient.isEmpty()) {
        return null;
      }

      Client client = maybeClient.get();
      OAuth2AuthorizationConsent.Builder consentBuilder =
          OAuth2AuthorizationConsent.withId(registeredClientId, principalName);
      client.scopes().forEach(scope -> consentBuilder.scope(scope.name().toLowerCase()));
      consentBuilder.scope("openid");

      return consentBuilder.build();
    }

    return null;
  }
}
