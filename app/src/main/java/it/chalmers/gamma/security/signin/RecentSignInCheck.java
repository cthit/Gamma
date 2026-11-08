package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.app.signin.domain.RecentSignIn;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class RecentSignInCheck implements RecentSignIn {

  private final SignInAuthorizationManagers signInAuthorizationManagers;

  public RecentSignInCheck(SignInAuthorizationManagers signInAuthorizationManagers) {
    this.signInAuthorizationManagers = signInAuthorizationManagers;
  }

  @Override
  public boolean isRecent() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return false;
    }

    SignInRequirements.forget(authentication.getName());
    AuthorizationResult result =
        this.signInAuthorizationManagers.recentlySignedIn().authorize(() -> authentication, null);
    return result != null && result.isGranted();
  }
}
