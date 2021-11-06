package it.chalmers.gamma.app.facade.internal;

import it.chalmers.gamma.app.facade.Facade;
import it.chalmers.gamma.app.usecase.AccessGuardUseCase;
import it.chalmers.gamma.app.repository.AppSettingsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserAgreementFacade extends Facade {

    private final AppSettingsRepository appSettingsRepository;

    public UserAgreementFacade(AccessGuardUseCase accessGuard, AppSettingsRepository appSettingsRepository) {
        super(accessGuard);
        this.appSettingsRepository = appSettingsRepository;
    }

    public void resetUserAgreement(String password) {
        this.accessGuard.require()
                .isAdmin()
                .and()
                .passwordCheck(password)
                .ifNotThrow();

        this.appSettingsRepository.setSettings(
                appSettingsRepository.getSettings().withLastUpdatedUserAgreement(Instant.now())
        );
    }

}
