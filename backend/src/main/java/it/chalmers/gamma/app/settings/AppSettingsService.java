package it.chalmers.gamma.app.settings;

import it.chalmers.gamma.app.domain.Settings;

public interface AppSettingsService {

    boolean hasSettings();
    void setSettings(Settings settings);

}
