package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.apikey.service.ApiKeyId;
import it.chalmers.gamma.domain.apikey.service.ApiKeyName;
import it.chalmers.gamma.domain.apikey.service.ApiKeyToken;
import it.chalmers.gamma.domain.apikey.service.ApiKeyDTO;
import it.chalmers.gamma.domain.client.service.ClientSecret;
import it.chalmers.gamma.domain.client.service.ClientDTO;

import it.chalmers.gamma.domain.text.data.dto.TextDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class TestClientBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestClientBootstrap.class);

    private final BootstrapConfig config;

    private final BootstrapServiceHelper helper;

    public TestClientBootstrap(BootstrapConfig bootstrapConfig, BootstrapServiceHelper bootstrapServiceHelper) {
        this.config = bootstrapConfig;
        this.helper = bootstrapServiceHelper;
    }

    void runOauthClient() {
        if (!this.helper.getClientFinder().clientExists(this.config.getOauth2ClientId())) {
            LOGGER.info("Creating test client...");

            TextDTO description = new TextDTO(
                    "Klient för att mocka " + this.config.getOauth2ClientName(),
                    "Client for mocking " + this.config.getOauth2ClientName()
            );

            this.helper.getClientService().create(
                    new ClientDTO(
                            this.config.getOauth2ClientId(),
                            new ClientSecret("{noop}" + this.config.getOauth2ClientSecret()),
                            this.config.getOauth2ClientRedirectUri(),
                            true,
                            this.config.getOauth2ClientName(),
                            description
                    )
            );

            TextDTO apiDescription = new TextDTO(
                    "API key",
                    "API key"
            );

            this.helper.getApiKeyService().create(
                    new ApiKeyDTO(
                            new ApiKeyId(),
                            new ApiKeyName(this.config.getOauth2ClientName()),
                            apiDescription,
                            new ApiKeyToken(this.config.getOauth2ClientApiKey())
                    )
            );

            LOGGER.info("Test client created!");
        }
    }
}
