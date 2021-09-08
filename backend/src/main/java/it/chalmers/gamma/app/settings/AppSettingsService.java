package it.chalmers.gamma.app.settings;

import it.chalmers.gamma.domain.Settings;

public interface AppSettingsService {

    boolean hasSettings();
    void setSettings(Settings settings);

}
