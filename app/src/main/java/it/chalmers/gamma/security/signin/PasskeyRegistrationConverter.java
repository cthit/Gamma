package it.chalmers.gamma.security.signin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.web.webauthn.jackson.WebauthnJacksonModule;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Spring Security's passkey JSON converter, which also rejects a registration whose label is
 * missing or too long. The registration filter answers 400 when the request can't be read.
 */
public class PasskeyRegistrationConverter implements HttpMessageConverter<Object> {

  private final JsonMapper mapper =
      JsonMapper.builder().addModule(new WebauthnJacksonModule()).build();
  private final JacksonJsonHttpMessageConverter delegate =
      new JacksonJsonHttpMessageConverter(this.mapper);

  @Override
  public boolean canRead(Class<?> clazz, MediaType mediaType) {
    return this.delegate.canRead(clazz, mediaType);
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    return this.delegate.canWrite(clazz, mediaType);
  }

  @Override
  public List<MediaType> getSupportedMediaTypes() {
    return this.delegate.getSupportedMediaTypes();
  }

  @Override
  public Object read(Class<?> clazz, HttpInputMessage inputMessage) throws IOException {
    byte[] body = inputMessage.getBody().readAllBytes();
    HttpInputMessage buffered =
        new HttpInputMessage() {
          @Override
          public InputStream getBody() {
            return new ByteArrayInputStream(body);
          }

          @Override
          public HttpHeaders getHeaders() {
            return inputMessage.getHeaders();
          }
        };

    if (clazz.getName().endsWith("WebAuthnRegistrationRequest")) {
      JsonNode label;
      try {
        label = this.mapper.readTree(body).path("publicKey").path("label");
      } catch (JacksonException e) {
        throw new HttpMessageNotReadableException("Not JSON", e, buffered);
      }
      if (!label.isString() || !PasskeyLabel.isValid(label.asString())) {
        throw new HttpMessageNotReadableException("Invalid passkey label", buffered);
      }
    }

    return this.delegate.read(clazz, buffered);
  }

  @Override
  public void write(Object object, MediaType contentType, HttpOutputMessage outputMessage)
      throws IOException {
    this.delegate.write(object, contentType, outputMessage);
  }
}
