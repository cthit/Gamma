package it.chalmers.gamma.adapter.secondary.jpa.secret;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;

class MasterSecretTest {

  @Test
  void matchesTheRfc5869TestVectorWithoutSalt() throws Exception {
    // RFC 5869, appendix A.3: SHA-256 with no salt and no info.
    byte[] ikm = new byte[22];
    Arrays.fill(ikm, (byte) 0x0b);
    assertEquals(
        "8da4e775a563c18f715f802a063c5a31b8a11f5c5ee1879ec3454e5f3c738d2d" + "9d201395faa4b61a96c8",
        HexFormat.of().formatHex(MasterSecret.hkdf(ikm, new byte[0], 42)));
  }

  @Test
  void derivesTheSameKeyForAPurposeAndIndependentKeysForOthers() {
    byte[] value = new byte[MasterSecret.BYTES];
    value[0] = 7;
    MasterSecret secret = new MasterSecret(value);
    byte[] totp = secret.deriveAesKey("Gamma TOTP secret v1").getEncoded();

    assertEquals(32, totp.length);
    assertEquals("AES", secret.deriveAesKey("Gamma TOTP secret v1").getAlgorithm());
    assertArrayEquals(
        totp, new MasterSecret(value).deriveAesKey("Gamma TOTP secret v1").getEncoded());
    assertFalse(Arrays.equals(totp, secret.deriveAesKey("Gamma other v1").getEncoded()));
    assertFalse(Arrays.equals(totp, value));

    value[1] = 1;
    assertFalse(
        Arrays.equals(
            totp, new MasterSecret(value).deriveAesKey("Gamma TOTP secret v1").getEncoded()));
  }

  @Test
  void rejectsSecretsOfTheWrongLength() {
    assertThrows(IllegalArgumentException.class, () -> new MasterSecret(null));
    assertThrows(IllegalArgumentException.class, () -> new MasterSecret(new byte[16]));
    assertThrows(IllegalArgumentException.class, () -> new MasterSecret(new byte[33]));
  }
}
