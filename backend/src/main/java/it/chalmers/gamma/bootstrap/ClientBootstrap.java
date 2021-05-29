package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.internal.client.service.ClientFinder;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.ClientSecret;
import it.chalmers.gamma.internal.client.service.ClientDTO;

import it.chalmers.gamma.internal.client.service.ClientService;
import it.chalmers.gamma.internal.text.service.TextDTO;
import it.chalmers.gamma.internal.user.service.UserFinder;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.internal.user.approval.service.UserApprovalDTO;
import it.chalmers.gamma.internal.user.approval.service.UserApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@DependsOn("mockBootstrap")
@Component
public class ClientBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);

    private final boolean mocking;
    private final String redirectUri;
    private final ClientFinder clientFinder;
    private final ClientService clientService;
    private final UserFinder userFinder;
    private final UserApprovalService userApprovalService;

    public ClientBootstrap(@Value("${application.mocking}") boolean mocking,
                           @Value("${application.default-oauth2-client.redirect-uri}") String redirectUri,
                           ClientFinder clientFinder,
                           ClientService clientService,
                           UserFinder userFinder,
                           UserApprovalService userApprovalService) {
        this.mocking = mocking;
        this.redirectUri = redirectUri;
        this.clientFinder = clientFinder;
        this.clientService = clientService;
        this.userFinder = userFinder;
        this.userApprovalService = userApprovalService;
    }


    void runOauthClient() {
        LOGGER.info("Creating test client...");

        if(!this.mocking || !this.clientFinder.getAll().isEmpty()) {
            return;
        }

        ClientId clientId = ClientId.valueOf("test");
        ClientSecret clientSecret = ClientSecret.valueOf("secret");
        ApiKeyToken apiKeyToken = ApiKeyToken.valueOf("test-api-key-secret-token");

        this.clientService.createWithApiKey(
                new ClientDTO(
                        clientId,
                        clientSecret,
                        redirectUri,
                        true,
                        EntityName.valueOf("test-client"),
                        new TextDTO("", "")
                ),
                apiKeyToken
        );

        for (UserRestrictedDTO user : this.userFinder.getAll()) {
            this.userApprovalService.create(
                    new UserApprovalDTO(
                            user.id(),
                            clientId
                    )
            );
        }

        LOGGER.info("Client generated with information:");
        LOGGER.info("ClientId: " + clientId.get());
        LOGGER.info("ClientSecret: " + clientSecret.get());
        LOGGER.info("Client redirect uri: " + this.redirectUri);
        LOGGER.info("An API key was also generated with the client, it has the token: " + apiKeyToken.get());
    }
}
