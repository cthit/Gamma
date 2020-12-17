package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.db.entity.Text;

import java.time.Instant;

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
        if (!this.helper.getItClientService().clientExists(this.config.getOauth2ClientId())) {
            LOGGER.info("Creating test client...");
            ITClient client = new ITClient();
            client.setName(this.config.getOauth2ClientName());
            Text description = new Text();
            description.setEn("Client for mocking " + this.config.getOauth2ClientName());
            description.setSv("Klient för att mocka " + this.config.getOauth2ClientName());
            client.setDescription(description);
            client.setWebServerRedirectUri(this.config.getOauth2ClientRedirectUri());
            client.setCreatedAt(Instant.now());
            client.setLastModifiedAt(Instant.now());
            client.setAccessTokenValidity(this.config.getAccessTokenValidityTime());
            client.setAutoApprove(true);
            client.setRefreshTokenValidity(this.config.getRefreshTokenValidityTime());
            client.setClientId(this.config.getOauth2ClientId());
            client.setClientSecret("{noop}" + this.config.getOauth2ClientSecret());
            this.helper.getItClientService().addITClient(client);
            Text apiDescription = new Text();
            apiDescription.setSv("API key");
            apiDescription.setEn("API key");
            this.helper.getApiKeyService().addApiKey(
                    this.config.getOauth2ClientName(),
                    this.config.getOauth2ClientApiKey(),
                    apiDescription
            );
            LOGGER.info("Test client created!");
        }
    }
}
