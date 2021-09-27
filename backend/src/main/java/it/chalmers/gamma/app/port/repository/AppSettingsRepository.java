package it.chalmers.gamma.app.port.repository;

import it.chalmers.gamma.app.domain.settings.Settings;

public interface AppSettingsRepository {

    boolean hasSettings();
    void setSettings(Settings settings);

}
