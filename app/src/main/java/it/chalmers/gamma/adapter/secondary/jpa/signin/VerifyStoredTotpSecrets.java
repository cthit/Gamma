package it.chalmers.gamma.adapter.secondary.jpa.signin;

import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Refuses to start if a stored authenticator app secret can't be decrypted, which happens when the
 * master secret in g_application_secret was lost or changed. Otherwise the affected users would
 * only find out when they fail to sign in.
 */
@Component
public class VerifyStoredTotpSecrets {

  private final UserTotpJpaRepository repository;
  private final TotpSecretCipher cipher;

  public VerifyStoredTotpSecrets(UserTotpJpaRepository repository, TotpSecretCipher cipher) {
    this.repository = repository;
    this.cipher = cipher;
  }

  @PostConstruct
  public void verifyStoredSecrets() {
    verify(this.repository.findAll(), this.cipher);
  }

  static void verify(List<UserTotpEntity> stored, TotpSecretCipher cipher) {
    for (UserTotpEntity entity : stored) {
      try {
        cipher.decrypt(entity.userId, entity.secret);
      } catch (IllegalStateException failure) {
        throw new IllegalStateException(
            "Can't decrypt the authenticator app secret of user "
                + entity.userId
                + ". Restore g_application_secret from a backup, or delete the rows in g_user_totp"
                + " that can't be decrypted.",
            failure);
      }
    }
  }
}
