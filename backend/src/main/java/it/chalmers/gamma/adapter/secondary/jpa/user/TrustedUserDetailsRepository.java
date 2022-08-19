package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.UserExtended;
import it.chalmers.gamma.app.user.domain.UserId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

public class TrustedUserDetailsRepository implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;
    private final SettingsRepository settingsRepository;

    public TrustedUserDetailsRepository(UserJpaRepository userJpaRepository, SettingsRepository settingsRepository) {
        this.userJpaRepository = userJpaRepository;
        this.settingsRepository = settingsRepository;
    }

    public GammaUser getGammaUserByUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserEntity userEntity = this.userJpaRepository.findById(UUID.fromString(user.getUsername())).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Settings settings = this.settingsRepository.getSettings();
        boolean acceptedUserAgreement = hasAcceptedLatestUserAgreement(userEntity.userAgreementAccepted, settings);

        return new GammaUser(
                new UserId(UUID.fromString(user.getUsername())),
                new Cid(userEntity.cid),
                new Nick(userEntity.nick),
                new FirstName(userEntity.firstName),
                new LastName(userEntity.lastName),
                new AcceptanceYear(userEntity.acceptanceYear),
                userEntity.language,
                new UserExtended(
                        new Email(userEntity.email),
                        userEntity.getVersion(),
                        acceptedUserAgreement,
                        userEntity.gdprTraining,
                        userEntity.locked,
                        userEntity.userAvatar == null ? null : new ImageUri(userEntity.userAvatar.avatarUri)
                )
        );
    }

    @Override
    public UserDetails loadUserByUsername(String userIdentifier) throws UsernameNotFoundException {
        UserEntity userEntity = this.userJpaRepository.findByCid(userIdentifier).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(
                userEntity.id.toString(),
                userEntity.password,
                true,
                true,
                true,
                !userEntity.locked,
                // Authorities will be loaded by UpdateUserPrincipalFilter
                Collections.emptyList()
        );
    }

    private boolean hasAcceptedLatestUserAgreement(Instant acceptedUserAgreement, Settings settings) {
        return settings.lastUpdatedUserAgreement().compareTo(acceptedUserAgreement) < 0;
    }

}
