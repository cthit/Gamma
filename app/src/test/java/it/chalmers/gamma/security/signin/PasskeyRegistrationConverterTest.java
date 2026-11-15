package it.chalmers.gamma.security.signin;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;

class PasskeyRegistrationConverterTest {

  private static final Class<?> REGISTRATION_REQUEST = registrationRequestClass();

  private final PasskeyRegistrationConverter converter = new PasskeyRegistrationConverter();

  @Test
  void aMissingBlankOrTooLongLabelCantBeRead() {
    for (String publicKey :
        new String[] {
          "{}",
          "{\"label\":null}",
          "{\"label\":7}",
          "{\"label\":\"   \"}",
          "{\"label\":\"" + "a".repeat(PasskeyLabel.MAX_LENGTH + 1) + "\"}"
        }) {
      assertThrows(
          HttpMessageNotReadableException.class,
          () -> read("{\"publicKey\":" + publicKey + "}"),
          publicKey);
    }
  }

  @Test
  void aLabelOfTheMaximumLengthIsPassedOn() {
    // There is no credential, so reading fails later, in Spring Security's own parsing.
    Exception e =
        assertThrows(
            Exception.class,
            () ->
                read(
                    "{\"publicKey\":{\"label\":\"" + "a".repeat(PasskeyLabel.MAX_LENGTH) + "\"}}"));
    assertFalse(
        e instanceof HttpMessageNotReadableException
            && "Invalid passkey label".equals(e.getMessage()));
  }

  private Object read(String json) throws Exception {
    byte[] body = json.getBytes(StandardCharsets.UTF_8);
    return this.converter.read(
        REGISTRATION_REQUEST,
        new HttpInputMessage() {
          @Override
          public InputStream getBody() {
            return new ByteArrayInputStream(body);
          }

          @Override
          public HttpHeaders getHeaders() {
            return new HttpHeaders();
          }
        });
  }

  private static Class<?> registrationRequestClass() {
    try {
      return Class.forName(
          "org.springframework.security.web.webauthn.registration."
              + "WebAuthnRegistrationFilter$WebAuthnRegistrationRequest");
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }
}
