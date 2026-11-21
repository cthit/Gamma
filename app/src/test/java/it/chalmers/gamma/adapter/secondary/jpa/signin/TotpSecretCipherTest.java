package it.chalmers.gamma.adapter.secondary.jpa.signin;

import static org.junit.jupiter.api.Assertions.*;

import it.chalmers.gamma.adapter.secondary.jpa.secret.MasterSecret;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TotpSecretCipherTest {
  private final UUID user = UUID.randomUUID();
  private final TotpSecretCipher cipher =
      new TotpSecretCipher(new MasterSecret(new byte[MasterSecret.BYTES]));

  @Test
  void encryptionRoundTripsWithFreshNonces() {
    String first = cipher.encrypt(user, "JBSWY3DPEHPK3PXP");
    String second = cipher.encrypt(user, "JBSWY3DPEHPK3PXP");
    assertNotEquals(first, second);
    assertEquals("JBSWY3DPEHPK3PXP", cipher.decrypt(user, first));
    assertEquals("JBSWY3DPEHPK3PXP", cipher.decrypt(user, second));
  }

  @Test
  void cannotMoveSecretsToAnotherAccount() {
    String encrypted = cipher.encrypt(user, "JBSWY3DPEHPK3PXP");
    assertThrows(IllegalStateException.class, () -> cipher.decrypt(UUID.randomUUID(), encrypted));
  }

  @Test
  void rejectsWrongKeyAndTamperedCiphertext() {
    String encrypted = cipher.encrypt(user, "JBSWY3DPEHPK3PXP");
    byte[] otherSecret = new byte[MasterSecret.BYTES];
    otherSecret[0] = 1;
    TotpSecretCipher other = new TotpSecretCipher(new MasterSecret(otherSecret));
    assertThrows(IllegalStateException.class, () -> other.decrypt(user, encrypted));
    byte[] stored = Base64.getDecoder().decode(encrypted.substring(3));
    stored[stored.length - 1] ^= 1;
    String tampered = "v1:" + Base64.getEncoder().encodeToString(stored);
    assertThrows(IllegalStateException.class, () -> cipher.decrypt(user, tampered));
  }

  @Test
  void rejectsPlaintextAndTruncatedCiphertext() {
    assertThrows(IllegalStateException.class, () -> cipher.decrypt(user, "JBSWY3DPEHPK3PXP"));
    assertThrows(IllegalStateException.class, () -> cipher.decrypt(user, "v1:AQ=="));
  }
}
