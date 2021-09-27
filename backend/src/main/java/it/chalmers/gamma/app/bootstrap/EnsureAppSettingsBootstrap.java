package it.chalmers.gamma.app.bootstrap;

import it.chalmers.gamma.app.domain.settings.Settings;
import it.chalmers.gamma.app.port.repository.AppSettingsRepository;
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

    public void ensureAppSettings() {
        if (!this.appSettingsRepository.hasSettings()) {
            Settings settings = new Settings(Instant.ofEpochSecond(0));
            this.appSettingsRepository.setSettings(settings);
            LOGGER.info("Adding default settings");
            LOGGER.info(String.valueOf(settings));
        }
    }

}
