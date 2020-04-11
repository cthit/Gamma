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

    private final BootstrapConfig cfg;

    private final BootstrapServiceHelper hlp;

    public TestClientBootstrap(BootstrapConfig cfg, BootstrapServiceHelper hlp) {
        this.cfg = cfg;
        this.hlp = hlp;
    }

    void runOauthClient() {
        if (!this.hlp.getItClientService().clientExists(this.cfg.getOauth2ClientId())) {
            LOGGER.info("Creating test client...");
            ITClient client = new ITClient();
            client.setName(this.cfg.getOauth2ClientName());
            Text description = new Text();
            description.setEn("Client for mocking " + this.cfg.getOauth2ClientName());
            description.setSv("Klient för att mocka " + this.cfg.getOauth2ClientName());
            client.setDescription(description);
            client.setWebServerRedirectUri(this.cfg.getOauth2ClientRedirectUri());
            client.setCreatedAt(Instant.now());
            client.setLastModifiedAt(Instant.now());
            client.setAccessTokenValidity(this.cfg.getAccessTokenValidityTime());
            client.setAutoApprove(this.cfg.isAutoApprove());
            client.setRefreshTokenValidity(this.cfg.getRefreshTokenValidityTime());
            client.setClientId(this.cfg.getOauth2ClientId());
            client.setClientSecret("{noop}" + this.cfg.getOauth2ClientSecret());
            this.hlp.getItClientService().addITClient(client);
            Text apiDescription = new Text();
            apiDescription.setSv("API key");
            apiDescription.setEn("API key");
            this.hlp.getApiKeyService().addApiKey(
                    this.cfg.getOauth2ClientName(),
                    this.cfg.getOauth2ClientApiKey(),
                    apiDescription
            );
            LOGGER.info("Test client created!");
        }
    }
}
