package it.chalmers.gamma.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.ssl.pem.PemContent;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

public record SigningKeys(RSAKey signingKey, List<RSAKey> verificationKeys) {

  static final int MINIMUM_KEY_SIZE = 2048;

  private static final Pattern PUBLIC_KEY =
      Pattern.compile(
          "-----BEGIN PUBLIC KEY-----([A-Za-z0-9+/=\\s]+)-----END PUBLIC KEY-----", Pattern.DOTALL);

  public SigningKeys {
    if (!signingKey.isPrivate()) {
      throw new IllegalArgumentException("The signing key must be a private key");
    }
    verificationKeys = verificationKeys.stream().map(RSAKey::toPublicJWK).toList();

    Set<String> keyIds = new HashSet<>();
    keyIds.add(signingKey.getKeyID());
    for (RSAKey key : verificationKeys) {
      if (!keyIds.add(key.getKeyID())) {
        throw new IllegalStateException(
            "The OAuth2 key " + key.getKeyID() + " is configured more than once");
      }
    }
  }

  public static SigningKeys load(OAuth2SigningKeyProperties properties) {
    RSAKey signingKey =
        properties.hasSigningKey()
            ? loadKey(properties.signingKey(), "OAUTH2_SIGNING_KEY", false)
            : generateKey();

    List<RSAKey> verificationKeys = new ArrayList<>();
    for (String key : properties.verificationKeys()) {
      verificationKeys.add(loadKey(key, "OAUTH2_VERIFICATION_KEYS", true));
    }

    return new SigningKeys(signingKey, verificationKeys);
  }

  public JWKSet publicJwkSet() {
    List<JWK> keys = new ArrayList<>();
    keys.add(signingKey.toPublicJWK());
    keys.addAll(verificationKeys);
    return new JWKSet(keys);
  }

  private static RSAKey generateKey() {
    try {
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
      generator.initialize(MINIMUM_KEY_SIZE);
      KeyPair keyPair = generator.generateKeyPair();
      return toJwk((RSAPublicKey) keyPair.getPublic(), (RSAPrivateCrtKey) keyPair.getPrivate());
    } catch (GeneralSecurityException | JOSEException e) {
      throw new IllegalStateException("Could not generate a temporary OAuth2 signing key", e);
    }
  }

  private static RSAKey loadKey(String value, String setting, boolean publicKeyAllowed) {
    String pem = read(value, setting);
    RSAPublicKey publicKey;
    RSAPrivateCrtKey privateKey = null;

    Matcher publicKeyMatcher = PUBLIC_KEY.matcher(pem);
    if (publicKeyMatcher.find()) {
      if (!publicKeyAllowed) {
        throw new IllegalStateException(setting + " must be a private key, not a public key");
      }
      publicKey = parsePublicKey(publicKeyMatcher.group(1), setting);
    } else {
      PrivateKey key = parsePrivateKey(pem, setting);
      if (!(key instanceof RSAPrivateCrtKey rsaKey)) {
        throw new IllegalStateException(setting + " must contain an RSA key");
      }
      privateKey = rsaKey;
      publicKey = derivePublicKey(rsaKey, setting);
    }

    int keySize = publicKey.getModulus().bitLength();
    if (keySize < MINIMUM_KEY_SIZE) {
      throw new IllegalStateException(
          setting
              + " contains a "
              + keySize
              + " bit RSA key, but keys must be at least "
              + MINIMUM_KEY_SIZE
              + " bits");
    }

    try {
      return toJwk(publicKey, privateKey);
    } catch (JOSEException e) {
      throw new IllegalStateException(setting + " contains a key that cannot be used", e);
    }
  }

  private static RSAKey toJwk(RSAPublicKey publicKey, RSAPrivateCrtKey privateKey)
      throws JOSEException {
    RSAKey.Builder builder =
        new RSAKey.Builder(publicKey).keyUse(KeyUse.SIGNATURE).algorithm(JWSAlgorithm.RS256);
    if (privateKey != null) {
      builder.privateKey(privateKey);
    }
    return builder.keyIDFromThumbprint().build();
  }

  private static String read(String value, String setting) {
    if (PemContent.isPresentInText(value)) {
      return value;
    }
    if (value.contains("-----") || value.contains("\n")) {
      throw new IllegalStateException(setting + " contains malformed PEM content");
    }

    String location = value.trim();
    Resource resource =
        ResourceUtils.isUrl(location)
            ? new DefaultResourceLoader().getResource(location)
            : new FileSystemResource(location);
    try (InputStream inputStream = resource.getInputStream()) {
      String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      if (!PemContent.isPresentInText(content)) {
        throw new IllegalStateException(location + " in " + setting + " is not a PEM file");
      }
      return content;
    } catch (IOException e) {
      throw new IllegalStateException("Could not read " + location + " in " + setting, e);
    }
  }

  private static RSAPublicKey parsePublicKey(String base64, String setting) {
    try {
      byte[] encoded = Base64.getMimeDecoder().decode(base64.strip());
      return (RSAPublicKey)
          KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
    } catch (GeneralSecurityException | IllegalArgumentException e) {
      throw new IllegalStateException(setting + " contains an invalid RSA public key", e);
    }
  }

  private static PrivateKey parsePrivateKey(String pem, String setting) {
    PrivateKey key;
    try {
      key = PemContent.of(pem).getPrivateKey();
    } catch (RuntimeException e) {
      throw new IllegalStateException(
          setting + " contains an invalid or password protected private key", e);
    }
    if (key == null) {
      throw new IllegalStateException(setting + " does not contain a private key");
    }
    return key;
  }

  private static RSAPublicKey derivePublicKey(RSAPrivateCrtKey privateKey, String setting) {
    try {
      return (RSAPublicKey)
          KeyFactory.getInstance("RSA")
              .generatePublic(
                  new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent()));
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException(setting + " contains an invalid RSA private key", e);
    }
  }
}
