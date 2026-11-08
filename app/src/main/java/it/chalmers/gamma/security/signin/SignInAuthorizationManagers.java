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

@Component
public class SignInAuthorizationManagers {

  private static final AuthenticationTrustResolver TRUST_RESOLVER =
      new AuthenticationTrustResolverImpl();

  private final SignInRequirements signInRequirements;

  public SignInAuthorizationManagers(SignInRequirements signInRequirements) {
    this.signInRequirements = signInRequirements;
  }

  public <T> AuthorizationManager<T> signedIn() {
    return (authentication, object) -> authorize(authentication, object, null, false);
  }

  public <T> AuthorizationManager<T> signedInUser() {
    return (authentication, object) -> {
      Authentication current = authentication.get();
      if (current != null && TRUST_RESOLVER.isAuthenticated(current) && !isUserSession(current)) {
        return new AuthorizationDecision(false);
      }
      return authorize(authentication, object, null, false);
    };
  }

  public <T> AuthorizationManager<T> recentlySignedIn() {
    return (authentication, object) ->
        authorize(authentication, object, SignInFactors.CONFIRMATION_VALIDITY, false);
  }

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

  public AuthorizationManager<RequestAuthorizationContext> mayAddPasskey() {
    return (authentication, object) -> {
      if (authentication.get() instanceof SignInSetupAuthenticationToken setup) {
        return new AuthorizationDecision(mayAddPasskeyDuringSetup(setup));
      }
      return authorize(authentication, object, null, true);
    };
  }

  private boolean mayAddPasskeyDuringSetup(SignInSetupAuthenticationToken setup) {
    if (!isSetupStillAllowed(setup)) {
      return false;
    }
    if (setup.getPrincipal().reason() != SignInSetupAuthentication.Reason.EMAIL) {
      return true;
    }
    return !this.signInRequirements
        .of(setup.getName())
        .map(UserSignInMethods::hasTotp)
        .orElse(true);
  }

  public boolean mayAddPasskeyNow(Authentication authentication) {
    AuthorizationResult result = mayAddPasskey().authorize(() -> authentication, null);
    return result != null && result.isGranted();
  }

  public boolean isSetupStillAllowed(SignInSetupAuthenticationToken setup) {
    if (setup.getPrincipal().isExpired()) {
      return false;
    }

    Optional<UserSignInMethods> methods = this.signInRequirements.of(setup.getName());
    if (methods.isEmpty() || methods.get().admin()) {
      return false;
    }

    return setup.getPrincipal().reason() != SignInSetupAuthentication.Reason.EMAIL
        || methods.get().emailRecovery();
  }

  public AuthorizationManager<RequestAuthorizationContext> signingIn() {
    return (authentication, object) ->
        new AuthorizationDecision(isUserSession(authentication.get()));
  }

  public AuthorizationManager<RequestAuthorizationContext> settingUpSignIn() {
    return (authentication, object) ->
        new AuthorizationDecision(
            authentication.get() instanceof SignInSetupAuthenticationToken setup
                && isSetupStillAllowed(setup));
  }

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
    Duration requiredValidity = addingPasskey ? SignInFactors.CONFIRMATION_VALIDITY : validity;

    return factors(user, current, requiredValidity, adminSettingUpPasskey)
        .authorize(authentication, object);
  }

  private static <T> AuthorizationManager<T> factors(
      UserSignInMethods user,
      Authentication current,
      @Nullable Duration validity,
      boolean signInLikeNonAdmin) {
    AllRequiredFactorsAuthorizationManager<T> passkey = require(validity, SignInFactors.PASSKEY);

    if ((user.admin() && !signInLikeNonAdmin) || !user.hasPassword()) {
      return passkey;
    }

    AllRequiredFactorsAuthorizationManager<T> password =
        user.hasTotp()
            ? require(validity, SignInFactors.PASSWORD, SignInFactors.TOTP)
            : require(validity, SignInFactors.PASSWORD);

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

  public static boolean isUserSession(@Nullable Authentication authentication) {
    return authentication != null
        && authentication.isAuthenticated()
        && (authentication instanceof UsernamePasswordAuthenticationToken
            || authentication instanceof WebAuthnAuthentication);
  }
}
