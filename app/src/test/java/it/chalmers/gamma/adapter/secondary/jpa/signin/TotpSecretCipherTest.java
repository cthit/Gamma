package it.chalmers.gamma.adapter.secondary.jpa.signin;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TotpSecretCipherTest {
  private final UUID user = UUID.randomUUID();
  private final TotpSecretCipher cipher =
      new TotpSecretCipher(Base64.getEncoder().encodeToString(new byte[32]));

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
    byte[] otherKey = new byte[32];
    otherKey[0] = 1;
    TotpSecretCipher other = new TotpSecretCipher(Base64.getEncoder().encodeToString(otherKey));
    assertThrows(IllegalStateException.class, () -> other.decrypt(user, encrypted));
    byte[] stored = Base64.getDecoder().decode(encrypted.substring(3));
    stored[stored.length - 1] ^= 1;
    String tampered = "v1:" + Base64.getEncoder().encodeToString(stored);
    assertThrows(IllegalStateException.class, () -> cipher.decrypt(user, tampered));
  }

  @Test
  void rejectsMissingInvalidOrShortKeysAndPlaintext() {
    assertThrows(IllegalArgumentException.class, () -> new TotpSecretCipher(""));
    assertThrows(IllegalArgumentException.class, () -> new TotpSecretCipher("not base64"));
    assertThrows(
        IllegalArgumentException.class,
        () -> new TotpSecretCipher(Base64.getEncoder().encodeToString(new byte[16])));
    assertThrows(IllegalStateException.class, () -> cipher.decrypt(user, "JBSWY3DPEHPK3PXP"));
    assertThrows(IllegalStateException.class, () -> cipher.decrypt(user, "v1:AQ=="));
  }
}
