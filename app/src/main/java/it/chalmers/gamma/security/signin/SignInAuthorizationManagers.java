package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import it.chalmers.gamma.security.signin.SignInRequirements.UserSignInMethods;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AllRequiredFactorsAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.webauthn.authentication.WebAuthnAuthentication;
import org.springframework.stereotype.Component;

/**
 * Decides which factors a user must have signed in with, using Spring Security's required factors:
 *
 * <ul>
 *   <li>Admins sign in with a passkey.
 *   <li>Everyone else signs in with a passkey, or with their password and, if they have an
 *       authenticator app, a code from it.
 * </ul>
 *
 * When factors are missing, Spring Security sends the user to the page for the first missing
 * factor, which is why the order of the alternatives matters.
 */
@Component
public class SignInAuthorizationManagers {

  private static final AuthenticationTrustResolver TRUST_RESOLVER =
      new AuthenticationTrustResolverImpl();

  private final SignInRequirements signInRequirements;

  public SignInAuthorizationManagers(SignInRequirements signInRequirements) {
    this.signInRequirements = signInRequirements;
  }

  /**
   * Fully signed in. Callers that aren't users, such as OAuth2 clients and bearer tokens, only need
   * to be authenticated.
   */
  public <T> AuthorizationManager<T> signedIn() {
    return (authentication, object) -> authorize(authentication, object, null, false);
  }

  /**
   * Signed in, and recently enough to change how they sign in. See {@link
   * SignInFactors#CONFIRMATION_VALIDITY}.
   */
  public <T> AuthorizationManager<T> recentlySignedIn() {
    return (authentication, object) ->
        authorize(authentication, object, SignInFactors.CONFIRMATION_VALIDITY, false);
  }

  /**
   * Signed in, and for users without a password also recently. Pages where users confirm with their
   * password that it is them use this, since users without a password confirm it by having recently
   * signed in with a passkey instead.
   */
  public <T> AuthorizationManager<T> recentlySignedInIfPasswordless() {
    return (authentication, object) -> {
      Authentication current = authentication.get();
      boolean passwordless =
          isUserSession(current)
              && this.signInRequirements
                  .of(current.getName())
                  .map(user -> !user.hasPassword())
                  .orElse(false);
      return authorize(
          authentication, object, passwordless ? SignInFactors.CONFIRMATION_VALIDITY : null, false);
    };
  }

  /**
   * May add a passkey. That needs a recent sign in, except for an admin without passkeys, who can't
   * get any further until they have added one. They only need to have signed in like everyone else
   * does.
   */
  public AuthorizationManager<RequestAuthorizationContext> mayAddPasskey() {
    return (authentication, object) -> {
      if (authentication.get() instanceof SignInSetupAuthenticationToken setup) {
        return new AuthorizationDecision(mayAddPasskeyDuringSetup(setup));
      }
      return authorize(authentication, object, null, true);
    };
  }

  /**
   * A link mailed to a user with an authenticator app only resets their password, so that the email
   * can't be used to get around the app.
   */
  private boolean mayAddPasskeyDuringSetup(SignInSetupAuthenticationToken setup) {
    if (setup.getPrincipal().reason() != SignInSetupAuthentication.Reason.EMAIL) {
      return true;
    }
    return !this.signInRequirements
        .of(setup.getName())
        .map(UserSignInMethods::hasTotp)
        .orElse(true);
  }

  /** Signed in with some factor, and possibly missing others. */
  public AuthorizationManager<RequestAuthorizationContext> signingIn() {
    return (authentication, object) ->
        new AuthorizationDecision(isUserSession(authentication.get()));
  }

  /** Only allowed to set up how they sign in. See {@link SignInSetupAuthentication}. */
  public AuthorizationManager<RequestAuthorizationContext> settingUpSignIn() {
    return (authentication, object) ->
        new AuthorizationDecision(authentication.get() instanceof SignInSetupAuthenticationToken);
  }

  /** Signed in with a password, and only waiting for the code of their authenticator app. */
  public AuthorizationManager<RequestAuthorizationContext> signingInWithTotp() {
    return (authentication, object) -> {
      Authentication current = authentication.get();
      if (!isUserSession(current)) {
        return new AuthorizationDecision(false);
      }

      boolean hasPassword = hasFactor(current, SignInFactors.PASSWORD);
      boolean hasTotp =
          this.signInRequirements
              .of(current.getName())
              .map(UserSignInMethods::hasTotp)
              .orElse(false);
      return new AuthorizationDecision(hasPassword && hasTotp);
    };
  }

  private <T> @Nullable AuthorizationResult authorize(
      Supplier<? extends @Nullable Authentication> authentication,
      T object,
      @Nullable Duration validity,
      boolean addingPasskey) {
    Authentication current = authentication.get();
    if (current == null || !TRUST_RESOLVER.isAuthenticated(current)) {
      return new AuthorizationDecision(false);
    }

    if (current instanceof SignInSetupAuthenticationToken) {
      // Only allowed to set up how they sign in, which their own rules allow.
      return new AuthorizationDecision(false);
    }

    if (!isUserSession(current)) {
      return new AuthorizationDecision(true);
    }

    Optional<UserSignInMethods> methods = this.signInRequirements.of(current.getName());
    if (methods.isEmpty()) {
      return new AuthorizationDecision(false);
    }

    UserSignInMethods user = methods.get();
    boolean adminSettingUpPasskey = addingPasskey && user.admin() && !user.hasPasskey();
    Duration requiredValidity =
        addingPasskey && !adminSettingUpPasskey ? SignInFactors.CONFIRMATION_VALIDITY : validity;

    return factors(user, current, requiredValidity, adminSettingUpPasskey)
        .authorize(authentication, object);
  }

  private static <T> AuthorizationManager<T> factors(
      UserSignInMethods user,
      Authentication current,
      @Nullable Duration validity,
      boolean signInLikeNonAdmin) {
    AllRequiredFactorsAuthorizationManager<T> passkey = require(validity, SignInFactors.PASSKEY);

    if (user.admin() && !signInLikeNonAdmin) {
      return passkey;
    }

    AllRequiredFactorsAuthorizationManager<T> password =
        user.hasTotp()
            ? require(validity, SignInFactors.PASSWORD, SignInFactors.TOTP)
            : require(validity, SignInFactors.PASSWORD);

    // Spring Security sends the user to the page of the first missing factor. A user who has
    // signed in with their password continues with it, for example with the code of their
    // authenticator app. Otherwise users with passkeys are asked for one, and everyone else for
    // their password.
    boolean passwordFirst =
        hasFactor(current, SignInFactors.PASSWORD)
            || (!hasFactor(current, SignInFactors.PASSKEY) && !user.hasPasskey());
    return passwordFirst
        ? AllRequiredFactorsAuthorizationManager.anyOf(password, passkey)
        : AllRequiredFactorsAuthorizationManager.anyOf(passkey, password);
  }

  private static boolean hasFactor(Authentication authentication, String factor) {
    return authentication.getAuthorities().stream()
        .anyMatch(authority -> factor.equals(authority.getAuthority()));
  }

  private static <T> AllRequiredFactorsAuthorizationManager<T> require(
      @Nullable Duration validity, String... factors) {
    AllRequiredFactorsAuthorizationManager.Builder<T> builder =
        AllRequiredFactorsAuthorizationManager.builder();
    for (String factor : factors) {
      builder.requireFactor(required -> {
        required.authority(factor);
        if (validity != null) {
          required.validDuration(validity);
        }
      });
    }
    return builder.build();
  }

  /** Whether the authentication is a user signed in to the web interface. */
  public static boolean isUserSession(@Nullable Authentication authentication) {
    return authentication != null
        && authentication.isAuthenticated()
        && (authentication instanceof UsernamePasswordAuthenticationToken
            || authentication instanceof WebAuthnAuthentication);
  }
}
