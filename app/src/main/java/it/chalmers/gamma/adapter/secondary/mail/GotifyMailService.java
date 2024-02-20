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
  private final boolean production;

  public GotifyMailService(
      @Value("${application.gotify.key}") String gotifyApiKey,
      @Value("${application.gotify.url}") String gotifyURL,
      @Value("${application.production}") boolean production) {
    this.gotifyApiKey = gotifyApiKey;
    this.gotifyURL = gotifyURL;
    this.production = production;
  }

  public void sendMail(String email, String subject, String body) {
    if (this.production) {
      sendMailViaGotify(email, subject, body);
    } else {
      LOGGER.warn(
          "Not in production environment, printing mail: \n "
              + "to: "
              + email
              + "\n"
              + "subject: "
              + subject
              + "\n"
              + "body: "
              + body);
    }
  }

  /**
   * Sends mail using Gotify Rest API, see https://github.com/cthit/gotify
   *
   * @return true if message was successfully sent false if not
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
        restTemplate.postForEntity(this.gotifyURL, entity, String.class);
    LOGGER.info("Gotify responded with " + response.getHeaders() + response.getBody());
  }
}
