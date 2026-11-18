package it.chalmers.gamma.adapter.secondary.jpa.secret;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import javax.crypto.KDF;
import javax.crypto.SecretKey;
import javax.crypto.spec.HKDFParameterSpec;

/**
 * Random secret that Gamma derives its encryption keys from. Each use derives its own key with
 * HKDF-SHA256 (RFC 5869) under a distinct purpose, so the keys are independent of each other.
 */
public final class MasterSecret {

  public static final int BYTES = 32;

  private final byte[] value;

  public MasterSecret(byte[] value) {
    if (value == null || value.length != BYTES) {
      throw new IllegalArgumentException("The master secret must be " + BYTES + " bytes");
    }
    this.value = value.clone();
  }

  /** Derives the AES-256 key for {@code purpose}, such as {@code "Gamma TOTP secret v1"}. */
  public SecretKey deriveAesKey(String purpose) {
    try {
      return KDF.getInstance("HKDF-SHA256")
          .deriveKey("AES", spec(this.value, purpose.getBytes(StandardCharsets.UTF_8), BYTES));
    } catch (GeneralSecurityException failure) {
      throw new IllegalStateException("Could not derive the key for " + purpose, failure);
    }
  }

  static byte[] hkdf(byte[] ikm, byte[] info, int length) throws GeneralSecurityException {
    return KDF.getInstance("HKDF-SHA256").deriveData(spec(ikm, info, length));
  }

  private static HKDFParameterSpec spec(byte[] ikm, byte[] info, int length) {
    // No salt: the input is already uniformly random, which RFC 5869 section 3.1 allows.
    return HKDFParameterSpec.ofExtract().addIKM(ikm).thenExpand(info, length);
  }

  @Override
  public String toString() {
    return "MasterSecret[redacted]";
  }
}
