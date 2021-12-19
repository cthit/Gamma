package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.Scope;
import it.chalmers.gamma.app.client.domain.WebServerRedirectUrl;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientSecret;

import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ClientBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);

    private final String redirectUrl;
    private final UserRepository userRepository;
    private final boolean mocking;
    private final ClientRepository clientRepository;

    public ClientBootstrap(@Value("${application.mocking}") boolean mocking,
                           @Value("${application.default-oauth2-client.redirect-url}") String redirectUrl,
                           UserRepository userRepository,
                           ClientRepository clientRepository) {
        this.redirectUrl = redirectUrl;
        this.mocking = mocking;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    public void runOauthClient() {
        if(!this.mocking || !this.clientRepository.getAll().isEmpty()) {
            return;
        }
        LOGGER.info("========== CLIENT BOOTSTRAP ==========");
        LOGGER.info("Creating test client...");

        ClientUid clientUid = ClientUid.generate();
        ClientId clientId = new ClientId("test");
        ClientSecret clientSecret = new ClientSecret("{noop}secret");
        ApiKeyToken apiKeyToken = new ApiKeyToken("test-api-key-secret-code");
        PrettyName prettyName = new PrettyName("test-client");

        List<User> allUsers = this.userRepository.getAll();

        this.clientRepository.save(
                new Client(
                        clientUid,
                        clientId,
                        clientSecret,
                        new WebServerRedirectUrl(redirectUrl),
                        prettyName,
                        new Text(),
                        new ArrayList<>(),
                        Arrays.asList(Scope.values()),
                        allUsers,
                        Optional.of(
                                new ApiKey(
                                        ApiKeyId.generate(),
                                        prettyName,
                                        new Text(),
                                        ApiKeyType.CLIENT,
                                        apiKeyToken
                                )
                        )
                )
        );

        LOGGER.info("Client generated with information:");
        LOGGER.info("ClientId: " + clientId.value());
        LOGGER.info("ClientSecret: " + clientSecret.value().substring("{noop}".length()));
        LOGGER.info("Client redirect uri: " + this.redirectUrl);
        LOGGER.info("An API key was also generated with the client, it has the code: " + apiKeyToken.value());
        LOGGER.info("==========                  ==========");
    }
}
