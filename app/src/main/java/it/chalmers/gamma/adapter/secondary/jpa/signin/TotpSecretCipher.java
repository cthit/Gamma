package it.chalmers.gamma.adapter.secondary.jpa.signin;

import it.chalmers.gamma.adapter.secondary.jpa.secret.MasterSecret;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import org.springframework.stereotype.Component;

@Component
public class TotpSecretCipher {
  private static final String PREFIX = "v1:";
  private static final int NONCE_BYTES = 12;
  private static final String PURPOSE_NAME = "Gamma TOTP secret v1";
  private static final byte[] PURPOSE = PURPOSE_NAME.getBytes(StandardCharsets.UTF_8);
  private final SecretKey key;
  private final SecureRandom random = new SecureRandom();

  public TotpSecretCipher(MasterSecret masterSecret) {
    this.key = masterSecret.deriveAesKey(PURPOSE_NAME);
  }

  public String encrypt(java.util.UUID userId, String secret) {
    byte[] nonce = new byte[NONCE_BYTES];
    this.random.nextBytes(nonce);
    try {
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(Cipher.ENCRYPT_MODE, this.key, new GCMParameterSpec(128, nonce));
      cipher.updateAAD(PURPOSE);
      cipher.updateAAD(userId.toString().getBytes(StandardCharsets.US_ASCII));
      byte[] ciphertext = cipher.doFinal(secret.getBytes(StandardCharsets.UTF_8));
      byte[] stored = Arrays.copyOf(nonce, nonce.length + ciphertext.length);
      System.arraycopy(ciphertext, 0, stored, nonce.length, ciphertext.length);
      return PREFIX + Base64.getEncoder().encodeToString(stored);
    } catch (GeneralSecurityException failure) {
      throw new IllegalStateException("Could not encrypt the authenticator app secret");
    }
  }

  public String decrypt(java.util.UUID userId, String stored) {
    if (stored == null || !stored.startsWith(PREFIX)) {
      throw new IllegalStateException("The authenticator app secret is not encrypted");
    }
    try {
      byte[] bytes = Base64.getDecoder().decode(stored.substring(PREFIX.length()));
      if (bytes.length < NONCE_BYTES + 16) {
        throw new IllegalArgumentException();
      }
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(
          Cipher.DECRYPT_MODE,
          this.key,
          new GCMParameterSpec(128, Arrays.copyOf(bytes, NONCE_BYTES)));
      cipher.updateAAD(PURPOSE);
      cipher.updateAAD(userId.toString().getBytes(StandardCharsets.US_ASCII));
      return new String(
          cipher.doFinal(bytes, NONCE_BYTES, bytes.length - NONCE_BYTES), StandardCharsets.UTF_8);
    } catch (GeneralSecurityException | IllegalArgumentException failure) {
      throw new IllegalStateException("Could not decrypt the authenticator app secret", failure);
    }
  }
}
