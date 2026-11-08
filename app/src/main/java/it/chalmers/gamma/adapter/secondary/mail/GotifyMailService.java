package it.chalmers.gamma.adapter.secondary.mail;

import it.chalmers.gamma.app.mail.domain.MailService;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class GotifyMailService implements MailService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GotifyMailService.class);
  private final String gotifyApiKey;
  private final String gotifyURL;
  private final ThrottlingService throttlingService;

  public GotifyMailService(
      @Value("${application.gotify.key}") String gotifyApiKey,
      @Value("${application.gotify.url}") String gotifyURL,
      ThrottlingService throttlingService) {
    this.gotifyApiKey = gotifyApiKey;
    this.gotifyURL = gotifyURL;
    this.throttlingService = throttlingService;
  }

  /** Sends mail using Gotify Rest API, see https://github.com/cthit/gotify */
  public void sendMail(String email, String subject, String body) {
    // Read the address from the request rather than the authentication details, since signed in
    // users get their details replaced with a UserAuthentication by UpdateUserPrincipalFilter.
    String clientIp = null;

    if (RequestContextHolder.getRequestAttributes()
        instanceof ServletRequestAttributes attributes) {
      clientIp = attributes.getRequest().getRemoteAddr();
    }

    if (clientIp != null && !throttlingService.canProceed("email:" + clientIp, 5)) {
      LOGGER.warn(
          "Client with IP: {} has exceeded the limit of number of emails that can be sent per day.",
          clientIp);
      return;
    } else if (clientIp == null) {
      LOGGER.error("Client IP is unexpectedly null.");
      return;
    }

    send(email, subject, body);
  }

  @Override
  public boolean sendSystemMail(String email, String subject, String body) {
    return send(email, subject, body);
  }

  public void sendSecurityNotification(String email, String subject, String body) {
    send(email, subject, body);
  }

  private boolean send(String email, String subject, String body) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, "pre-shared: " + this.gotifyApiKey);
    headers.setContentType(MediaType.APPLICATION_JSON);

    record Request(String to, String from, String subject, String body) {}

    Request request = new Request(email, "no-reply@chalmers.it", subject, body);

    HttpEntity<Request> entity = new HttpEntity<>(request, headers);
    RestTemplate restTemplate = new RestTemplate();
    try {
      restTemplate.postForEntity(this.gotifyURL + "/mail", entity, String.class);
      return true;
    } catch (RestClientException e) {
      LOGGER.error(e.getMessage());
      return false;
    }
  }
}
