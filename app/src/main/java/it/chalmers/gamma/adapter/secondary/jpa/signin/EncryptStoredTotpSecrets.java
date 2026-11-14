package it.chalmers.gamma.adapter.secondary.jpa.signin;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/** Upgrades secrets stored by earlier revisions of the unreleased passkey/MFA feature. */
@Component
public class EncryptStoredTotpSecrets {
  private final UserTotpJpaRepository repository;
  private final TotpSecretCipher cipher;

  public EncryptStoredTotpSecrets(UserTotpJpaRepository repository, TotpSecretCipher cipher) {
    this.repository = repository;
    this.cipher = cipher;
  }

  @PostConstruct
  public void encryptExistingSecrets() {
    for (UserTotpEntity entity : this.repository.findAll()) {
      if (this.cipher.isEncrypted(entity.secret)) {
        // Refuse to start with the wrong key, rather than silently locking users out later.
        this.cipher.decrypt(entity.userId, entity.secret);
      } else {
        this.repository.encryptLegacySecret(
            entity.userId, entity.secret, this.cipher.encrypt(entity.userId, entity.secret));
      }
    }
  }
}
