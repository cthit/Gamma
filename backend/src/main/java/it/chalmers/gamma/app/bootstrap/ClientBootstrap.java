package it.chalmers.gamma.app.bootstrap;

import it.chalmers.gamma.app.domain.apikey.ApiKey;
import it.chalmers.gamma.app.domain.apikey.ApiKeyId;
import it.chalmers.gamma.app.domain.apikey.ApiKeyType;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.common.Text;
import it.chalmers.gamma.app.port.repository.ClientRepository;
import it.chalmers.gamma.app.usecase.ClientUserApprovalUseCase;
import it.chalmers.gamma.app.domain.apikey.ApiKeyToken;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.app.domain.client.ClientSecret;

import it.chalmers.gamma.app.facade.ClientFacade;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.port.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class ClientBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);

    private final String redirectUri;
    private final ClientFacade clientFacade;
    private final ClientUserApprovalUseCase userApprovalService;
    private final UserRepository userRepository;
    private final boolean mocking;
    private final ClientRepository clientRepository;

    public ClientBootstrap(ClientFacade clientFacade,
                           @Value("${application.mocking}") boolean mocking,
                           @Value("${application.default-oauth2-client.redirect-uri}") String redirectUri,
                           ClientUserApprovalUseCase userApprovalService,
                           UserRepository userRepository,
                           ClientRepository clientRepository) {
        this.redirectUri = redirectUri;
        this.clientFacade = clientFacade;
        this.mocking = mocking;
        this.userApprovalService = userApprovalService;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    public void runOauthClient() {
        if(!this.mocking || !this.clientFacade.getAll().isEmpty()) {
            return;
        }
        LOGGER.info("========== CLIENT BOOTSTRAP ==========");
        LOGGER.info("Creating test client...");


        ClientId clientId = ClientId.valueOf("test");
        ClientSecret clientSecret = new ClientSecret("secret");
        ApiKeyToken apiKeyToken = new ApiKeyToken("test-api-key-secret-token");
        PrettyName prettyName = new PrettyName("test-client");

        this.clientRepository.create(
                new Client(
                        clientId,
                        clientSecret,
                        redirectUri,
                        true,
                        prettyName,
                        new Text(),
                        new ArrayList<>(),
                        new ArrayList<>(),
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

        for (User user : this.userRepository.getAll()) {
            this.userApprovalService.approveUserForClient(user.cid(), clientId);
        }

        LOGGER.info("Client generated with information:");
        LOGGER.info("ClientId: " + clientId.getValue());
        LOGGER.info("ClientSecret: " + clientSecret.value());
        LOGGER.info("Client redirect uri: " + this.redirectUri);
        LOGGER.info("An API key was also generated with the client, it has the token: " + apiKeyToken.value());
        LOGGER.info("========== CLIENT BOOTSTRAP ==========");
    }
}
