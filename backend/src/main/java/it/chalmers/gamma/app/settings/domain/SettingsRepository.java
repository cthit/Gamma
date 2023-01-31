package it.chalmers.gamma.app.settings.domain;

public interface SettingsRepository {

    boolean hasSettings();

    Settings getSettings();

    void setSettings(UpdateSettings updateSettings);

    void setSettings(Settings settings);

    interface UpdateSettings {
        Settings updateSettings(Settings settings);
    }

}
