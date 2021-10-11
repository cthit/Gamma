package it.chalmers.gamma.app.repository;

import it.chalmers.gamma.app.domain.settings.Settings;

public interface AppSettingsRepository {

    boolean hasSettings();
    void setSettings(Settings settings);
    Settings getSettings();

}
