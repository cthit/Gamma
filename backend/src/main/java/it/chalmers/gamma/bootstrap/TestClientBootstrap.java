package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.apikey.data.ApiKeyDTO;
import it.chalmers.gamma.domain.client.ClientSecret;
import it.chalmers.gamma.domain.client.data.ClientDTO;
import it.chalmers.gamma.domain.client.data.ClientDetailsDTO;
import it.chalmers.gamma.domain.text.Text;

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

            Text description = new Text();
            description.setEn("Client for mocking " + this.config.getOauth2ClientName());
            description.setSv("Klient för att mocka " + this.config.getOauth2ClientName());

            this.helper.getClientService().createClient(
                    new ClientDTO(
                            this.config.getOauth2ClientId(),
                            new ClientSecret("{noop}" + this.config.getOauth2ClientSecret()),
                            this.config.getOauth2ClientRedirectUri(),
                            true,
                            this.config.getOauth2ClientName(),
                            description
                    )
            );

            Text apiDescription = new Text();
            apiDescription.setSv("API key");
            apiDescription.setEn("API key");

            this.helper.getApiKeyService().createApiKey(
                    new ApiKeyDTO(
                            this.config.getOauth2ClientName(),
                            apiDescription,
                            this.config.getOauth2ClientApiKey()
                    )
            );

            LOGGER.info("Test client created!");
        }
    }
}
