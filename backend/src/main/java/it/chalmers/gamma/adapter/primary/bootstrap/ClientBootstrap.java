package it.chalmers.gamma.adapter.primary.bootstrap;

import it.chalmers.gamma.app.domain.ApiKeyToken;
import it.chalmers.gamma.app.domain.ClientId;
import it.chalmers.gamma.app.domain.ClientSecret;
import it.chalmers.gamma.app.domain.Client;

import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.app.client.ClientService;
import it.chalmers.gamma.app.domain.Text;
import it.chalmers.gamma.app.domain.UserApproval;
import it.chalmers.gamma.app.user.UserApprovalService;
import it.chalmers.gamma.app.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@DependsOn("mockBootstrap")
@Component
public class ClientBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);

    private final String redirectUri;
    private final ClientService clientService;
    private final UserApprovalService userApprovalService;
    private final UserService userService;
    private final boolean mocking;

    public ClientBootstrap(ClientService clientService,
                           UserApprovalService userApprovalService,
                           UserService userService,
                           @Value("${application.mocking}") boolean mocking,
                           @Value("${application.default-oauth2-client.redirect-uri}") String redirectUri) {
        this.redirectUri = redirectUri;
        this.clientService = clientService;
        this.userApprovalService = userApprovalService;
        this.userService = userService;
        this.mocking = mocking;
    }

    @PostConstruct
    void runOauthClient() {
        if(!this.mocking || !this.clientService.getAll().isEmpty()) {
            return;
        }
        LOGGER.info("========== CLIENT BOOTSTRAP ==========");
        LOGGER.info("Creating test client...");


        ClientId clientId = ClientId.valueOf("test");
        ClientSecret clientSecret = new ClientSecret("secret");
        ApiKeyToken apiKeyToken = new ApiKeyToken("test-api-key-secret-token");

        this.clientService.createWithApiKey(
                new Client(
                        clientId,
                        redirectUri,
                        true,
                        new PrettyName("test-client"),
                        new Text()
                ),
                clientSecret,
                apiKeyToken,
                new ArrayList<>());

        for (UserRestricted user : this.userService.getAll()) {
            this.userApprovalService.create(
                    new UserApproval(
                            user.id(),
                            clientId
                    )
            );
        }

        LOGGER.info("Client generated with information:");
        LOGGER.info("ClientId: " + clientId.value());
        LOGGER.info("ClientSecret: " + clientSecret.value());
        LOGGER.info("Client redirect uri: " + this.redirectUri);
        LOGGER.info("An API key was also generated with the client, it has the token: " + apiKeyToken.value());
        LOGGER.info("========== CLIENT BOOTSTRAP ==========");
    }
}
