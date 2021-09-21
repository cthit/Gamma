package it.chalmers.gamma.app.settings;

import it.chalmers.gamma.domain.Settings;

public interface AppSettingsRepository {

    boolean hasSettings();
    void setSettings(Settings settings);

}
