package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientSecret;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.Scope;
import it.chalmers.gamma.app.client.domain.WebServerRedirectUrl;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static it.chalmers.gamma.DomainFactory.addAll;
import static it.chalmers.gamma.DomainFactory.u1;
import static it.chalmers.gamma.DomainFactory.u2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@DataJpaTest
@Import({ClientRepositoryAdapter.class,
        ClientEntityConverter.class,
        UserRepositoryAdapter.class,
        UserEntityConverter.class,
        ApiKeyEntityConverter.class})
public class ClientEntityIntegrationTests {

    @Autowired
    private ClientRepositoryAdapter clientRepositoryAdapter;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void Given_AValidClient_Expect_save_To_Work() {
        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new WebServerRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    @Test
    public void Given_AValidClientWithScopes_Expect_save_To_Work() {
        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new WebServerRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                List.of(Scope.PROFILE, Scope.EMAIL),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    @Test
    public void Given_AValidClientWithApiKey_Expect_save_To_Work() {
        ApiKey apiKey = new ApiKey(
                ApiKeyId.generate(),
                new PrettyName("Mat"),
                new Text(
                        "Api nyckel för mat",
                        "Api key for mat"
                ),
                ApiKeyType.CLIENT,
                ApiKeyToken.generate()
        );

        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new WebServerRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.of(apiKey)
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    @Test
    public void Given_AValidClientWithRestrictions_Expect_save_To_Work() {
        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new WebServerRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                List.of(new AuthorityLevelName("admin")),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    @Test
    public void Given_AValidClientWithApprovedUsers_Expect_save_To_Work() {
        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new WebServerRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);

        addAll(userRepository, u1, u2);

        Client newClient2 = newClient.withApprovedUsers(List.of(u1, u2));

        this.clientRepositoryAdapter.save(newClient2);

        Client savedClient2 = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient2)
                .isEqualTo(newClient2);
    }

    @Test
    public void Given_AValidClient_Expect_save_And_delete_To_Work() throws ClientRepository.ClientNotFoundException {
        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new WebServerRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        assertThat(this.clientRepositoryAdapter.getAll())
                .containsExactlyInAnyOrder(newClient);

        this.clientRepositoryAdapter.delete(uid);

        assertThat(this.clientRepositoryAdapter.getAll())
                .isEmpty();
    }

    @Test
    public void Given_InvalidClient_Expect_delete_To_Throw() {
        assertThatExceptionOfType(ClientRepository.ClientNotFoundException.class)
                .isThrownBy(() -> this.clientRepositoryAdapter.delete(ClientUid.generate()));
    }

    @Test
    public void Given_AValidClient_Expect_get_WithClientId_To_Work() {
        ClientId clientId = ClientId.generate();
        Client newClient = new Client(
                ClientUid.generate(),
                clientId,
                ClientSecret.generate(),
                new WebServerRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(clientId).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    

}
