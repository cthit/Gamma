package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.authentication.UserExtendedGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.Password;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserExtended;
import it.chalmers.gamma.app.user.domain.UserId;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserEntityConverter {

    private final UserExtendedGuard userExtendedGuard;
    private final SettingsRepository SettingsRepository;

    public UserEntityConverter(UserExtendedGuard userExtendedGuard,
                               SettingsRepository SettingsRepository) {
        this.userExtendedGuard = userExtendedGuard;
        this.SettingsRepository = SettingsRepository;
    }

    public User toDomain(UserEntity userEntity) {
        UserExtended extended = null;

        Settings settings = this.SettingsRepository.getSettings();

        UserId userId = new UserId(userEntity.id);

        if (userExtendedGuard.accessToExtended(userId)) {
            extended = new UserExtended(
                    new Email(userEntity.email),
                    userEntity.getVersion(),
                    new Password(userEntity.password),
                    hasAcceptedLatestUserAgreement(userEntity.userAgreementAccepted, settings),
                    userEntity.gdprTraining,
                    userEntity.locked,
                    userEntity.userAvatar == null ? null : new ImageUri(userEntity.userAvatar.avatarUri)
            );
        }

        return new User(
                userId,
                new Cid(userEntity.cid),
                new Nick(userEntity.nick),
                new FirstName(userEntity.firstName),
                new LastName(userEntity.lastName),
                new AcceptanceYear(userEntity.acceptanceYear),
                userEntity.language,
                extended
        );
    }

    private boolean hasAcceptedLatestUserAgreement(Instant acceptedUserAgreement, Settings settings) {
        return settings.lastUpdatedUserAgreement().compareTo(acceptedUserAgreement) < 0;
    }

}
