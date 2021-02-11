package it.chalmers.gamma.mail;

import org.json.simple.JSONObject;
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
public class MailSenderService {

    @Value("${application.gotify.key}")
    private String gotifyApiKey;

    @Value("${application.gotify.url}")
    private String gotifyURL;

    @Value("${application.production}")
    private boolean production;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailSenderService.class);

    public boolean trySendingMail(String email, String subject, String body) {
        if (this.production) {
            return sendMail(email, subject, body);
        } else {
            LOGGER.warn("Not in production environment, printing mail: \n "
                    + "to: " + email + "\n"
                    + "subject: " + subject + "\n"
                    + "body: " + body);
            return false;
        }
    }

    /**
     * Sends mail using Gotify Rest API, see https://github.com/cthit/gotify
     *
     * @return true if message was successfully sent false if not
     */
    public boolean sendMail(String email,  String subject, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "pre-shared: " + this.gotifyApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject object = new JSONObject();
        object.put("to", email);
        object.put("from", "no-reply@chalmers.it");
        object.put("subject", subject);
        object.put("body", body);

        HttpEntity<JSONObject> entity = new HttpEntity<>(object, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(this.gotifyURL, entity, String.class);
        LOGGER.info("Gotify responded with " + response.getHeaders() + response.getBody());
        return true;
    }

}
