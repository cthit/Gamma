package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthorityLevelBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAuthorityLevelBootstrap.class);

    private final AuthorityLevelRepository authorityLevelRepository;
    private final BootstrapSettings bootstrapSettings;

    public AdminAuthorityLevelBootstrap(AuthorityLevelRepository authorityLevelRepository,
                                        BootstrapSettings bootstrapSettings) {
        this.authorityLevelRepository = authorityLevelRepository;
        this.bootstrapSettings = bootstrapSettings;
    }

    public void ensureAdminAuthorityLevel() {
        if (!this.bootstrapSettings.adminSetup()) {
            LOGGER.info("Admin setup is disabled. I hope you know what you're doing...");
            return;
        }

        String admin = "admin";
        try {
            this.authorityLevelRepository.create(new AuthorityLevelName(admin));
        } catch (AuthorityLevelRepository.AuthorityLevelAlreadyExistsException e) {
            // Ignore
        }
    }

}
