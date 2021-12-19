package it.chalmers.gamma.app.settings.domain;

public interface SettingsRepository {

    boolean hasSettings();
    void setSettings(Settings settings);
    Settings getSettings();

}
