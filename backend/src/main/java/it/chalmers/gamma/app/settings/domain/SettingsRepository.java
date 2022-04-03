package it.chalmers.gamma.app.settings.domain;

public interface SettingsRepository {

    boolean hasSettings();
    void setSettings(UpdateSettings updateSettings);
    void setSettings(Settings settings);
    Settings getSettings();

    interface UpdateSettings {
        Settings updateSettings(Settings settings);
    }

}
