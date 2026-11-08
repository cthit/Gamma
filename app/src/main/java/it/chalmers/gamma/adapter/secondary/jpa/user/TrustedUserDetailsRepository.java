package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.user.domain.*;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TrustedUserDetailsRepository implements UserDetailsService {

  private final UserJpaRepository userJpaRepository;

  public TrustedUserDetailsRepository(UserJpaRepository userJpaRepository) {
    this.userJpaRepository = userJpaRepository;
  }

  public GammaUser getGammaUser(UUID userId, Authentication authentication) {
    UserEntity userEntity =
        this.userJpaRepository
            .findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (userEntity.locked || isRevoked(authentication, userEntity.sessionsRevokedAt)) {
      throw new UsernameNotFoundException("Session is no longer valid");
    }

    return new GammaUser(
        new UserId(userId),
        new Cid(userEntity.cid),
        new Nick(userEntity.nick),
        new FirstName(userEntity.firstName),
        new LastName(userEntity.lastName),
        new AcceptanceYear(userEntity.acceptanceYear),
        userEntity.language,
        new UserExtended(
            new Email(userEntity.email),
            userEntity.getVersion(),
            userEntity.locked,
            userEntity.userAvatar == null ? null : new ImageUri(userEntity.userAvatar.avatarUri)));
  }

  /**
   * Whether the session signed in at or before {@code revokedAt}. Spring Security records when the
   * password or passkey was checked as the issue time of its factor. A session without one is from
   * before that was recorded, so it is treated as revoked as well.
   */
  private static boolean isRevoked(Authentication authentication, @Nullable Instant revokedAt) {
    if (revokedAt == null) {
      return false;
    }

    Optional<Instant> signedInAt =
        authentication.getAuthorities().stream()
            .filter(FactorGrantedAuthority.class::isInstance)
            .map(FactorGrantedAuthority.class::cast)
            .filter(factor ->
                FactorGrantedAuthority.PASSWORD_AUTHORITY.equals(factor.getAuthority())
                    || FactorGrantedAuthority.WEBAUTHN_AUTHORITY.equals(factor.getAuthority()))
            .map(FactorGrantedAuthority::getIssuedAt)
            .max(Instant::compareTo);

    return signedInAt.isEmpty() || !signedInAt.get().isAfter(revokedAt);
  }

  @Override
  public UserDetails loadUserByUsername(String userIdentifier) throws UsernameNotFoundException {
    UserEntity userEntity =
        getUserByUsernameOrEmail(userIdentifier)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (userEntity.password == null) {
      // Same error to masquerade the account being locked
      throw new UsernameNotFoundException("User credentials needs to be created");
    }

    return new User(
        userEntity.id.toString(),
        userEntity.password,
        true,
        true,
        true,
        !userEntity.locked,
        Collections.emptyList());
  }

  private Optional<UserEntity> getUserByUsernameOrEmail(String userIdentifier) {
    Optional<UserEntity> userEntity = this.userJpaRepository.findByCidIgnoreCase(userIdentifier);
    if (userEntity.isEmpty()) {
      userEntity = this.userJpaRepository.findByEmailIgnoreCase(userIdentifier);
    }

    return userEntity;
  }
}
