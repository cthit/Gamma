package it.chalmers.gamma.adapter.secondary.jpa.appsettings;

import it.chalmers.gamma.app.settings.AppSettingsRepository;
import it.chalmers.gamma.domain.Settings;
import org.springframework.stereotype.Service;

@Service
public class AppSettingsRepositoryAdapter implements AppSettingsRepository {
    @Override
    public boolean hasSettings() {
        return false;
    }

    @Override
    public void setSettings(Settings settings) {

    }
}
