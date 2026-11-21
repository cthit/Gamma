package it.chalmers.gamma.security.signin;

import java.util.Set;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.webauthn.api.Bytes;
import org.springframework.security.web.webauthn.api.CredentialRecord;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialRpEntity;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.RelyingPartyAuthenticationRequest;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.security.web.webauthn.management.Webauthn4JRelyingPartyOperations;

/** Binds a discoverable assertion's user handle to the owner of the signing credential. */
public class UserBoundRelyingPartyOperations extends Webauthn4JRelyingPartyOperations {

  private final UserCredentialRepository credentials;

  public UserBoundRelyingPartyOperations(
      PublicKeyCredentialUserEntityRepository users,
      UserCredentialRepository credentials,
      PublicKeyCredentialRpEntity relyingParty,
      Set<String> origins) {
    super(users, credentials, relyingParty, origins);
    this.credentials = credentials;
  }

  @Override
  public PublicKeyCredentialUserEntity authenticate(RelyingPartyAuthenticationRequest request) {
    CredentialRecord credential =
        this.credentials.findByCredentialId(request.getPublicKey().getRawId());
    Bytes handle = request.getPublicKey().getResponse().getUserHandle();
    boolean discoverable =
        request.getRequestOptions().getAllowCredentials() == null
            || request.getRequestOptions().getAllowCredentials().isEmpty();
    if (credential == null
        || (handle == null && discoverable)
        || (handle != null && !handle.equals(credential.getUserEntityUserId()))) {
      throw new BadCredentialsException("Invalid passkey user handle");
    }
    return super.authenticate(request);
  }
}
