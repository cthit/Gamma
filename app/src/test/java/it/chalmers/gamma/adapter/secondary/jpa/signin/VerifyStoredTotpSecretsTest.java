package it.chalmers.gamma.adapter.secondary.jpa.signin;

import static org.junit.jupiter.api.Assertions.*;

import it.chalmers.gamma.adapter.secondary.jpa.secret.MasterSecret;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VerifyStoredTotpSecretsTest {
  private static final String SECRET = "JBSWY3DPEHPK3PXP";

  private final UUID user = UUID.randomUUID();
  private final TotpSecretCipher cipher = cipher(0);

  @Test
  void acceptsSecretsEncryptedWithTheCurrentMasterSecret() {
    List<UserTotpEntity> stored =
        List.of(new UserTotpEntity(user, cipher.encrypt(user, SECRET), 0));
    assertDoesNotThrow(() -> VerifyStoredTotpSecrets.verify(stored, cipher));
  }

  @Test
  void refusesSecretsEncryptedWithAnotherMasterSecret() {
    List<UserTotpEntity> stored =
        List.of(new UserTotpEntity(user, cipher(1).encrypt(user, SECRET), 0));
    IllegalStateException failure =
        assertThrows(
            IllegalStateException.class, () -> VerifyStoredTotpSecrets.verify(stored, cipher));
    assertTrue(failure.getMessage().contains(user.toString()));
  }

  private static TotpSecretCipher cipher(int firstByte) {
    byte[] secret = new byte[MasterSecret.BYTES];
    secret[0] = (byte) firstByte;
    return new TotpSecretCipher(new MasterSecret(secret));
  }
}
