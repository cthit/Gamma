package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.Settings;
import it.chalmers.gamma.app.settings.AppSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;

@Component
public class EnsureAppSettingsBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsureAppSettingsBootstrap.class);

    private final AppSettingsService appSettingsService;

    public EnsureAppSettingsBootstrap(AppSettingsService appSettingsService) {
        this.appSettingsService = appSettingsService;
    }

    @PostConstruct
    public void ensureAppSettings() {
        if (!this.appSettingsService.hasSettings()) {
            Settings settings = new Settings(Instant.ofEpochSecond(0));
            this.appSettingsService.setSettings(settings);
            LOGGER.info("Adding default settings");
            LOGGER.info(String.valueOf(settings));
        }
    }

}
