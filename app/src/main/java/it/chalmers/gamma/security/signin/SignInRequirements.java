package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.signin.domain.PasskeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/** Looks up what a user must sign in with. */
@Component
public class SignInRequirements {

  private static final String REQUEST_ATTRIBUTE = SignInRequirements.class.getName();

  private final AdminRepository adminRepository;
  private final PasskeyRepository passkeyRepository;
  private final TotpRepository totpRepository;
  private final UserRepository userRepository;

  public SignInRequirements(
      AdminRepository adminRepository,
      PasskeyRepository passkeyRepository,
      TotpRepository totpRepository,
      UserRepository userRepository) {
    this.adminRepository = adminRepository;
    this.passkeyRepository = passkeyRepository;
    this.totpRepository = totpRepository;
    this.userRepository = userRepository;
  }

  /**
   * The user's sign in methods, looked up once per request.
   *
   * @param name the name of the user's authentication, which is their user id
   */
  public Optional<UserSignInMethods> of(String name) {
    UserId userId;
    try {
      userId = new UserId(UUID.fromString(name));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }

    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    String key = REQUEST_ATTRIBUTE + "." + name;
    if (attributes != null
        && attributes.getAttribute(key, RequestAttributes.SCOPE_REQUEST)
            instanceof UserSignInMethods cached) {
      return Optional.of(cached);
    }

    UserSignInMethods methods;
    try {
      methods =
          new UserSignInMethods(
              userId,
              this.adminRepository.isAdmin(userId),
              this.userRepository.hasPassword(userId),
              this.passkeyRepository.hasAny(userId),
              this.totpRepository.isEnabled(userId));
    } catch (UserRepository.UserNotFoundException e) {
      return Optional.empty();
    }

    if (attributes != null) {
      attributes.setAttribute(key, methods, RequestAttributes.SCOPE_REQUEST);
    }
    return Optional.of(methods);
  }

  public record UserSignInMethods(
      UserId userId, boolean admin, boolean hasPassword, boolean hasPasskey, boolean hasTotp) {}
}
