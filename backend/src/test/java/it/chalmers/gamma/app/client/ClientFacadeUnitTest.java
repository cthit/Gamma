package it.chalmers.gamma.app.client;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.client.domain.*;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class ClientFacadeUnitTest {

    @Mock
    private AccessGuard accessGuard;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientFacade clientFacade;

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void Given_AValidClientWithoutApiKey_Expect_create_To_ReturnValidSecrets() {
        ClientFacade.NewClient newClient = new ClientFacade.NewClient(
                "https://mat.chalmers.it",
                "Mat",
                "Klient för Mat",
                "Client for mat",
                false,
                Collections.emptyList(),
                false
        );

        ClientFacade.ClientAndApiKeySecrets secrets = this.clientFacade.create(newClient);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        Client capturedNewClient = captor.getValue();

        //If not null, then they are valid.
        Assertions.assertThat(capturedNewClient.clientUid())
                .isNotNull();
        Assertions.assertThat(capturedNewClient.clientId())
                .isNotNull();

        Assertions.assertThat(secrets.apiKeyToken())
                .isNull();

        Client expectedClient = new Client(
                capturedNewClient.clientUid(),
                capturedNewClient.clientId(),
                new ClientSecret(secrets.clientSecret()),
                new RedirectUrl(newClient.webServerRedirectUrl()),
                new PrettyName(newClient.prettyName()),
                new Text(
                        newClient.svDescription(),
                        newClient.enDescription()
                ),
                Collections.emptyList(),
                List.of(Scope.PROFILE),
                Collections.emptyList(),
                Optional.empty()
        );

        Assertions.assertThat(capturedNewClient)
                .isEqualTo(expectedClient);

        InOrder inOrder = inOrder(accessGuard, clientRepository);

        //Makes sure that isAdmin is called first.
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(clientRepository).save(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_AValidClientWithoutApiKeyAndEmailScope_Expect_create_To_ReturnValidSecrets() {
        ClientFacade.NewClient newClient = new ClientFacade.NewClient(
                "https://mat.chalmers.it",
                "Mat",
                "Klient för Mat",
                "Client for mat",
                false,
                Collections.emptyList(),
                true
        );

        ClientFacade.ClientAndApiKeySecrets secrets = this.clientFacade.create(newClient);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        Client capturedNewClient = captor.getValue();

        //If not null, then they are valid.
        Assertions.assertThat(capturedNewClient.clientUid())
                .isNotNull();
        Assertions.assertThat(capturedNewClient.clientId())
                .isNotNull();

        Assertions.assertThat(secrets.apiKeyToken())
                .isNull();

        Client expectedClient = new Client(
                capturedNewClient.clientUid(),
                capturedNewClient.clientId(),
                new ClientSecret(secrets.clientSecret()),
                new RedirectUrl(newClient.webServerRedirectUrl()),
                new PrettyName(newClient.prettyName()),
                new Text(
                        newClient.svDescription(),
                        newClient.enDescription()
                ),
                Collections.emptyList(),
                List.of(Scope.PROFILE, Scope.EMAIL),
                Collections.emptyList(),
                Optional.empty()
        );

        Assertions.assertThat(capturedNewClient)
                .isEqualTo(expectedClient);
    }

    @Test
    public void Given_AValidClientWithApiKey_Expect_create_To_ReturnValidSecrets() {
        ClientFacade.NewClient newClient = new ClientFacade.NewClient(
                "https://mat.chalmers.it",
                "Mat",
                "Klient för Mat",
                "Client for mat",
                true,
                Collections.emptyList(),
                true
        );

        ClientFacade.ClientAndApiKeySecrets secrets = this.clientFacade.create(newClient);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        Client capturedNewClient = captor.getValue();

        //If not null, then they are valid.
        Assertions.assertThat(capturedNewClient.clientUid())
                .isNotNull();
        Assertions.assertThat(capturedNewClient.clientId())
                .isNotNull();
        Assertions.assertThat(capturedNewClient.clientApiKey())
                .isNotEmpty();

        Assertions.assertThat(secrets.apiKeyToken())
                .isNotNull();

        ApiKey actualApiKey = capturedNewClient.clientApiKey().get();

        ApiKey expectedApiKey = new ApiKey(
                actualApiKey.id(),
                new PrettyName(newClient.prettyName()),
                new Text(
                        "Api nyckel för klienten: " + newClient.prettyName(),
                        "Api key for client: " + newClient.prettyName()
                ),
                ApiKeyType.CLIENT,
                new ApiKeyToken(secrets.apiKeyToken())
        );

        Client expectedClient = new Client(
                capturedNewClient.clientUid(),
                capturedNewClient.clientId(),
                new ClientSecret(secrets.clientSecret()),
                new RedirectUrl(newClient.webServerRedirectUrl()),
                new PrettyName(newClient.prettyName()),
                new Text(
                        newClient.svDescription(),
                        newClient.enDescription()
                ),
                Collections.emptyList(),
                List.of(Scope.PROFILE, Scope.EMAIL),
                Collections.emptyList(),
                Optional.of(expectedApiKey)
        );

        Assertions.assertThat(capturedNewClient)
                .isEqualTo(expectedClient);
    }

    @Test
    public void Given_AValidClientWithoutApiKeyAndRestrictions_Expect_create_To_ReturnValidSecrets() {
        ClientFacade.NewClient newClient = new ClientFacade.NewClient(
                "https://mat.chalmers.it",
                "Mat",
                "Klient för Mat",
                "Client for mat",
                false,
                List.of("mat"),
                false
        );

        ClientFacade.ClientAndApiKeySecrets secrets = this.clientFacade.create(newClient);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        Client capturedNewClient = captor.getValue();

        //If not null, then they are valid.
        Assertions.assertThat(capturedNewClient.clientUid())
                .isNotNull();
        Assertions.assertThat(capturedNewClient.clientId())
                .isNotNull();

        Assertions.assertThat(secrets.apiKeyToken())
                .isNull();

        Client expectedClient = new Client(
                capturedNewClient.clientUid(),
                capturedNewClient.clientId(),
                new ClientSecret(secrets.clientSecret()),
                new RedirectUrl(newClient.webServerRedirectUrl()),
                new PrettyName(newClient.prettyName()),
                new Text(
                        newClient.svDescription(),
                        newClient.enDescription()
                ),
                List.of(new AuthorityLevelName("mat")),
                List.of(Scope.PROFILE),
                Collections.emptyList(),
                Optional.empty()
        );

        Assertions.assertThat(capturedNewClient)
                .isEqualTo(expectedClient);
    }

    @Test
    public void Given_AValidClientUID_Expect_delete_To_NotThrow() throws ClientFacade.ClientNotFoundException, ClientRepository.ClientNotFoundException {
        ClientUid clientUid = ClientUid.generate();

        this.clientFacade.delete(clientUid.value().toString());

        ArgumentCaptor<ClientUid> captor = ArgumentCaptor.forClass(ClientUid.class);
        verify(clientRepository).delete(captor.capture());
        ClientUid capturedClientUid = captor.getValue();

        Assertions.assertThat(capturedClientUid)
                .isEqualTo(clientUid);

        InOrder inOrder = inOrder(accessGuard, clientRepository);

        //Makes sure that isAdmin is called first.
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(clientRepository).delete(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_InvalidClient_Expect_delete_To_Throw() throws ClientRepository.ClientNotFoundException {
        doThrow(ClientRepository.ClientNotFoundException.class)
                .when(clientRepository).delete(any());

        assertThatExceptionOfType(ClientFacade.ClientNotFoundException.class)
                .isThrownBy(() -> this.clientFacade.delete(ClientUid.generate().getValue()));
    }

}