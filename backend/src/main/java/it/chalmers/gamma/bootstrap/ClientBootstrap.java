package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.client.domain.*;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.property.DefaultOAuth2Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ClientBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);
    private final BootstrapSettings bootstrapSettings;
    private final DefaultOAuth2Client defaultOAuth2Client;
    private final ClientRepository clientRepository;

    public ClientBootstrap(BootstrapSettings bootstrapSettings,
                           DefaultOAuth2Client defaultOAuth2Client,
                           ClientRepository clientRepository) {
        this.bootstrapSettings = bootstrapSettings;
        this.defaultOAuth2Client = defaultOAuth2Client;
        this.clientRepository = clientRepository;
    }

    public void runOauthClient() {
        if (!this.bootstrapSettings.mocking() || !this.clientRepository.getAll().isEmpty()) {
            return;
        }
        LOGGER.info("========== CLIENT BOOTSTRAP ==========");
        LOGGER.info("Creating test client...");

        ClientUid clientUid = ClientUid.generate();
        ClientId clientId = new ClientId(defaultOAuth2Client.clientId());
        ClientSecret clientSecret = new ClientSecret("{noop}" + defaultOAuth2Client.clientSecret());
        ApiKeyToken apiKeyToken = new ApiKeyToken(defaultOAuth2Client.apiKey());
        PrettyName prettyName = new PrettyName(defaultOAuth2Client.clientName());
        ClientRedirectUrl clientRedirectUrl = new ClientRedirectUrl(defaultOAuth2Client.redirectUrl());

        this.clientRepository.save(
                new Client(
                        clientUid,
                        clientId,
                        clientSecret,
                        clientRedirectUrl,
                        prettyName,
                        new Text(),
                        Arrays.stream(defaultOAuth2Client.scopes().split(","))
                                .map(String::toUpperCase)
                                .map(Scope::valueOf)
                                .toList(),
                        new ApiKey(
                                ApiKeyId.generate(),
                                prettyName,
                                new Text(),
                                ApiKeyType.CLIENT,
                                apiKeyToken
                        ),
                        new ClientOwnerOfficial(),
                        null
                )
        );

        LOGGER.info("Client generated with information:");
        LOGGER.info("ClientId: " + clientId.value());
        LOGGER.info("ClientSecret: " + clientSecret.value().substring("{noop}".length()));
        LOGGER.info("Client redirect uri: " + clientRedirectUrl.value());
        LOGGER.info("An API key was also generated with the client, it has the code: " + apiKeyToken.value());
        LOGGER.info("==========                  ==========");
    }
}
