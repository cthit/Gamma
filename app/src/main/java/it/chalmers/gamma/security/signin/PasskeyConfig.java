package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import java.util.Collections;
import java.util.UUID;
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
import org.springframework.security.web.webauthn.management.JdbcUserCredentialRepository;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.security.web.webauthn.management.WebAuthnRelyingPartyOperations;
import org.springframework.security.web.webauthn.management.Webauthn4JRelyingPartyOperations;

/**
 * Passkeys, through Spring Security's WebAuthn support. Spring Security stores the passkeys in the
 * user_entities and user_credentials tables, and knows each user by their user id.
 */
@Configuration
public class PasskeyConfig {

  @Bean
  public PublicKeyCredentialUserEntityRepository publicKeyCredentialUserEntityRepository(
      JdbcOperations jdbcOperations) {
    return new JdbcPublicKeyCredentialUserEntityRepository(jdbcOperations);
  }

  @Bean
  public UserCredentialRepository userCredentialRepository(JdbcOperations jdbcOperations) {
    return new JdbcUserCredentialRepository(jdbcOperations);
  }

  @Bean
  public WebAuthnRelyingPartyOperations webAuthnRelyingPartyOperations(
      PublicKeyCredentialUserEntityRepository userEntities,
      UserCredentialRepository userCredentials,
      UserJpaRepository userJpaRepository,
      PasskeyProperties passkeyProperties,
      @Value("${application.base-url}") String baseUrl) {
    Webauthn4JRelyingPartyOperations operations =
        new Webauthn4JRelyingPartyOperations(
            userEntities,
            userCredentials,
            PublicKeyCredentialRpEntity.builder()
                .id(passkeyProperties.rpIdOr(baseUrl))
                .name("Gamma")
                .build(),
            passkeyProperties.allowedOriginsOr(baseUrl));

    operations.setCustomizeCreationOptions(options -> {
      // A passkey is a whole sign in on its own, so the authenticator must verify the user, for
      // example with a fingerprint or PIN, and not only that someone is present.
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

  /**
   * Spring Security names the passkey after the user id, which password managers would show the
   * user. The passkey is given the user's cid and name instead. Only the id of the user entity is
   * stored with the passkey, so this doesn't change how the user is found.
   */
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

  /**
   * Finds the user that signed in with a passkey, by their user id. Spring Security doesn't check
   * whether the account is locked, so this does.
   */
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
