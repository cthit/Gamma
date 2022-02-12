package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserExtended;
import it.chalmers.gamma.app.user.domain.UserId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserEntityConverter {

    private final UserAccessGuard userAccessGuard;
    private final SettingsRepository SettingsRepository;

    public UserEntityConverter(UserAccessGuard userAccessGuard,
                               SettingsRepository SettingsRepository) {
        this.userAccessGuard = userAccessGuard;
        this.SettingsRepository = SettingsRepository;
    }

    @Nullable
    public User toDomain(UserEntity userEntity) {
        Settings settings = this.SettingsRepository.getSettings();
        UserId userId = new UserId(userEntity.id);
        boolean acceptedUserAgreement = hasAcceptedLatestUserAgreement(userEntity.userAgreementAccepted, settings);

        /* If the user is locked or has not accepted the latest user agreement then nothing should be returned
         * unless the signed-in user is the actual user being converted or if the signed-in user is an admin.
         */
        if (userEntity.locked || !acceptedUserAgreement) {
            if (!(userAccessGuard.isMe(userId) || userAccessGuard.isAdmin())) {
                return null;
            }
        }

        UserExtended extended = null;
        if (userAccessGuard.accessToExtended(userId)) {
            extended = new UserExtended(
                    new Email(userEntity.email),
                    userEntity.getVersion(),
                    acceptedUserAgreement,
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
