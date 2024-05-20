package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.user.domain.*;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TrustedUserDetailsRepository implements UserDetailsService {

  private final UserJpaRepository userJpaRepository;

  public TrustedUserDetailsRepository(UserJpaRepository userJpaRepository) {
    this.userJpaRepository = userJpaRepository;
  }

  public GammaUser getGammaUserByUser() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    UserEntity userEntity =
        this.userJpaRepository
            .findById(UUID.fromString(user.getUsername()))
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (userEntity.password == null) {
      // Same error to masquerade the account being locked
      throw new UsernameNotFoundException("User credentials needs to be created");
    }

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
            userEntity.locked,
            userEntity.userAvatar == null ? null : new ImageUri(userEntity.userAvatar.avatarUri)));
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
        // Authorities will be loaded by UpdateUserPrincipalFilter
        Collections.emptyList());
  }

  private Optional<UserEntity> getUserByUsernameOrEmail(String userIdentifier) {
    Optional<UserEntity> userEntity = this.userJpaRepository.findByCid(userIdentifier);
    if (userEntity.isEmpty()) {
      userEntity = this.userJpaRepository.findByEmail(userIdentifier);
    }

    return userEntity;
  }
}
