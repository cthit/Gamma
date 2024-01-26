package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;

@Component
public class EnsureSettingsBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsureSettingsBootstrap.class);

    private final SettingsRepository settingsRepository;

    public EnsureSettingsBootstrap(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public void ensureAppSettings() {
        if (!this.settingsRepository.hasSettings()) {
            LOGGER.info("========== ENSURE APP SETTINGS BOOTSTRAP ==========");

            Settings settings = new Settings(
                    Collections.emptyList()
            );
            this.settingsRepository.setSettings(settings);
            LOGGER.info("Adding default settings");
            LOGGER.info(String.valueOf(settings));

            LOGGER.info("==========                               ==========");
        }
    }

}
