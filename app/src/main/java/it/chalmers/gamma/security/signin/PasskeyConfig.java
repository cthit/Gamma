package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.webauthn.api.AuthenticatorSelectionCriteria;
import org.springframework.security.web.webauthn.api.ImmutablePublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialRpEntity;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.api.ResidentKeyRequirement;
import org.springframework.security.web.webauthn.api.UserVerificationRequirement;
import org.springframework.security.web.webauthn.management.JdbcPublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.security.web.webauthn.management.WebAuthnRelyingPartyOperations;
import org.springframework.security.web.webauthn.management.Webauthn4JRelyingPartyOperations;

@Configuration
public class PasskeyConfig {

  @Bean
  public PublicKeyCredentialUserEntityRepository publicKeyCredentialUserEntityRepository(
      JdbcOperations jdbcOperations) {
    return new JdbcPublicKeyCredentialUserEntityRepository(jdbcOperations);
  }

  @Bean
  public UserCredentialRepository userCredentialRepository(
      JdbcOperations jdbcOperations, ObjectProvider<PasskeyRegistrationGuard> registrationGuard) {
    return new PasskeyCredentialRepository(jdbcOperations, registrationGuard);
  }

  @Bean
  public WebAuthnRelyingPartyOperations webAuthnRelyingPartyOperations(
      PublicKeyCredentialUserEntityRepository userEntities,
      UserCredentialRepository userCredentials,
      UserJpaRepository userJpaRepository,
      @Value("${application.base-url}") String baseUrl) {
    URI base = URI.create(baseUrl);
    Webauthn4JRelyingPartyOperations operations =
        new UserBoundRelyingPartyOperations(
            userEntities,
            userCredentials,
            PublicKeyCredentialRpEntity.builder().id(base.getHost()).name("Gamma").build(),
            Set.of(base.getScheme() + "://" + base.getRawAuthority()));

    operations.setCustomizeCreationOptions(options -> {
      options.authenticatorSelection(
          AuthenticatorSelectionCriteria.builder()
              .residentKey(ResidentKeyRequirement.REQUIRED)
              .userVerification(UserVerificationRequirement.REQUIRED)
              .build());

      PublicKeyCredentialUserEntity shownUser =
          shownUser(
              userEntities,
              userJpaRepository,
              SecurityContextHolder.getContext().getAuthentication());
      if (shownUser != null) {
        options.user(shownUser);
      }
    });
    operations.setCustomizeRequestOptions(
        options -> options.userVerification(UserVerificationRequirement.REQUIRED));

    return operations;
  }

  private static PublicKeyCredentialUserEntity shownUser(
      PublicKeyCredentialUserEntityRepository userEntities,
      UserJpaRepository userJpaRepository,
      Authentication authentication) {
    if (authentication == null) {
      return null;
    }

    PublicKeyCredentialUserEntity userEntity =
        userEntities.findByUsername(authentication.getName());
    if (userEntity == null) {
      return null;
    }

    UUID userId;
    try {
      userId = UUID.fromString(authentication.getName());
    } catch (IllegalArgumentException e) {
      return null;
    }

    return userJpaRepository
        .findById(userId)
        .map(user ->
            (PublicKeyCredentialUserEntity)
                ImmutablePublicKeyCredentialUserEntity.builder()
                    .id(userEntity.getId())
                    .name(user.cid())
                    .displayName(user.firstName() + " '" + user.nick() + "' " + user.lastName())
                    .build())
        .orElse(null);
  }

  public static UserDetailsService passkeyUserDetailsService(UserJpaRepository userJpaRepository) {
    return name -> {
      UUID userId;
      try {
        userId = UUID.fromString(name);
      } catch (IllegalArgumentException e) {
        throw new UsernameNotFoundException("Not a user id");
      }

      UserEntity user =
          userJpaRepository
              .findById(userId)
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));
      if (user.locked()) {
        throw new UsernameNotFoundException("The account is locked");
      }

      return new User(name, "", true, true, true, true, Collections.emptyList());
    };
  }
}
