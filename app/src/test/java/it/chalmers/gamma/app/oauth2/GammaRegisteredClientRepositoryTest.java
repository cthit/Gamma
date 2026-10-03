package it.chalmers.gamma.app.oauth2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import it.chalmers.gamma.app.client.domain.*;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

@ExtendWith(MockitoExtension.class)
class GammaRegisteredClientRepositoryTest {

  @Mock ClientRepository clientRepository;

  @InjectMocks GammaRegisteredClientRepository repository;

  @Test
  void requireProofKeyShouldBeFalse() {
    ClientId clientId = ClientId.generate();

    Client client =
        new Client(
            ClientUid.generate(),
            clientId,
            new ClientSecret("{bcrypt}dummyhashvalue"),
            new ClientRedirectUrl("https://example.com/callback"),
            new PrettyName("Test Client"),
            new Text("sv", "en"),
            List.of(Scope.PROFILE),
            null,
            new ClientOwnerOfficial(),
            null);

    when(clientRepository.get(any(ClientId.class))).thenReturn(Optional.of(client));

    RegisteredClient registeredClient = repository.findByClientId(clientId.value());

    assertThat(registeredClient).isNotNull();
    assertThat(registeredClient.getClientSettings().isRequireProofKey()).isFalse();
    assertThat(registeredClient.getClientSettings().isRequireAuthorizationConsent()).isTrue();
  }
}
