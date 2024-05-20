package it.chalmers.gamma.adapter.secondary.mail;

import it.chalmers.gamma.app.mail.domain.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GotifyMailService implements MailService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GotifyMailService.class);
  private final String gotifyApiKey;
  private final String gotifyURL;

  public GotifyMailService(
      @Value("${application.gotify.key}") String gotifyApiKey,
      @Value("${application.gotify.url}") String gotifyURL) {
    this.gotifyApiKey = gotifyApiKey;
    this.gotifyURL = gotifyURL;
  }

  public void sendMail(String email, String subject, String body) {
      sendMailViaGotify(email, subject, body);
  }

  /**
   * Sends mail using Gotify Rest API, see https://github.com/cthit/gotify
   */
  private void sendMailViaGotify(String email, String subject, String body) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, "pre-shared: " + this.gotifyApiKey);
    headers.setContentType(MediaType.APPLICATION_JSON);

    record Request(String to, String from, String subject, String body) {}

    Request request = new Request(email, "no-reply@chalmers.it", subject, body);

    HttpEntity<Request> entity = new HttpEntity<>(request, headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response =
        restTemplate.postForEntity(this.gotifyURL + "/mail", entity, String.class);
    LOGGER.info("Gotify responded with " + response.getHeaders() + response.getBody());
  }
}
