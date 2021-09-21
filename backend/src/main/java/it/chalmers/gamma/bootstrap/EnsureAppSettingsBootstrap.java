package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.Settings;
import it.chalmers.gamma.app.settings.AppSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;

@Component
public class EnsureAppSettingsBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsureAppSettingsBootstrap.class);

    private final AppSettingsRepository appSettingsRepository;

    public EnsureAppSettingsBootstrap(AppSettingsRepository appSettingsRepository) {
        this.appSettingsRepository = appSettingsRepository;
    }

    @PostConstruct
    public void ensureAppSettings() {
        if (!this.appSettingsRepository.hasSettings()) {
            Settings settings = new Settings(Instant.ofEpochSecond(0));
            this.appSettingsRepository.setSettings(settings);
            LOGGER.info("Adding default settings");
            LOGGER.info(String.valueOf(settings));
        }
    }

}
