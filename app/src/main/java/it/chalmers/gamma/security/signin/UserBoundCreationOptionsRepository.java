package it.chalmers.gamma.security.signin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialCreationOptions;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.registration.HttpSessionPublicKeyCredentialCreationOptionsRepository;
import org.springframework.security.web.webauthn.registration.PublicKeyCredentialCreationOptionsRepository;

public class UserBoundCreationOptionsRepository
    implements PublicKeyCredentialCreationOptionsRepository {

  private final PublicKeyCredentialCreationOptionsRepository delegate =
      new HttpSessionPublicKeyCredentialCreationOptionsRepository();

  private final PublicKeyCredentialUserEntityRepository userEntities;

  public UserBoundCreationOptionsRepository(PublicKeyCredentialUserEntityRepository userEntities) {
    this.userEntities = userEntities;
  }

  @Override
  public void save(
      HttpServletRequest request,
      HttpServletResponse response,
      @Nullable PublicKeyCredentialCreationOptions options) {
    this.delegate.save(request, response, options);
  }

  @Override
  public @Nullable PublicKeyCredentialCreationOptions load(HttpServletRequest request) {
    PublicKeyCredentialCreationOptions options = this.delegate.load(request);
    if (options == null) {
      return null;
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    PublicKeyCredentialUserEntity current =
        this.userEntities.findByUsername(authentication.getName());
    if (current == null || !current.getId().equals(options.getUser().getId())) {
      return null;
    }

    return options;
  }
}
