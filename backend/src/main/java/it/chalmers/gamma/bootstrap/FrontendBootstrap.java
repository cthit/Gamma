package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.db.entity.Text;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class FrontendBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontendBootstrap.class);

    private final BootstrapConfig cfg;

    private final BootstrapServiceHelper hlp;

    public FrontendBootstrap(BootstrapConfig cfg, BootstrapServiceHelper hlp) {
        this.cfg = cfg;
        this.hlp = hlp;
    }

    void runFrontendClientDetails() {
        if (!this.hlp.getItClientService().clientExists(this.cfg.getClientId())) {
            LOGGER.info("Creating frontend client...");
            Text description = new Text();
            description.setEn("The client details for the frontend of Gamma");
            description.setSv("Klient detaljerna för Gammas frontend");
            ITClient client = new ITClient();
            client.setClientId(this.cfg.getClientId());
            client.setClientSecret("{noop}secret");
            client.setAutoApprove(true);
            client.setName("Gamma Frontend");
            client.setCreatedAt(Instant.now());
            client.setLastModifiedAt(Instant.now());
            client.setRefreshTokenValidity(0);
            client.setWebServerRedirectUri(this.cfg.getRedirectUri().trim());
            client.setDescription(description);
            client.setAccessTokenValidity(60 * 60 * 24 * 30);
            this.hlp.getItClientService().addITClient(client);
            LOGGER.info("Frontend client created!");
        }
    }
}
