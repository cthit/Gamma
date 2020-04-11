package it.chalmers.gamma.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * This class adds a superadmin on startup if one does not already exist, to make sure one
 * always exists, and to make development easier. It also adds mock data from /mock/mock.json
 */
@Component
public class Bootstrap implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    private final BootstrapConfig cfg;

    private final BootstrapServiceHelper hlp;

    private final AdminBootstrap adminBootstrap;

    private final FrontendBootstrap frontendBootstrap;

    private final MockBootstrap mockBootstrap;

    private final TestClientBootstrap testClientBootstrap;

    public Bootstrap(BootstrapConfig cfg,
                     BootstrapServiceHelper hlp,
                     AdminBootstrap adminBootstrap,
                     FrontendBootstrap frontendBootstrap,
                     MockBootstrap mockBootstrap,
                     TestClientBootstrap testClientBootstrap) {
        this.cfg = cfg;
        this.hlp = hlp;
        this.adminBootstrap = adminBootstrap;
        this.frontendBootstrap = frontendBootstrap;
        this.mockBootstrap = mockBootstrap;
        this.testClientBootstrap = testClientBootstrap;
    }

    @Override
    public void run(String... args) {
        if (shouldRunBootstrap()) {
            LOGGER.info("No admin user, running Bootstrap...");
            this.adminBootstrap.runAdminBootstrap();
            this.frontendBootstrap.runFrontendClientDetails();

            if (this.cfg.isMocking()) {
                LOGGER.info("Mock enabled...");
                this.mockBootstrap.runMockBootstrap();
                this.testClientBootstrap.runOauthClient();
                LOGGER.info("Mock finished!");
            }
            LOGGER.info("Bootstrap finished!");
        }
    }

    private boolean shouldRunBootstrap() {
        return !this.hlp.getUserService().userExists("admin");
    }

}
