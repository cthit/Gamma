package it.chalmers.gamma.service;

import java.util.Objects;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public final class MailSenderService {

    @Value("${application.gotify.key}")
    private String gotifyApiKey;

    @Value("${application.gotify.url}")
    private String gotifyURL;

    private MailSenderService() {
    }

    /**
     * Sends mail using Gotify Rest API, see https://github.com/cthit/gotify
     *
     * @return true if message was successfully sent false if not
     */
    public boolean sendMail(String cid, String subject, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "pre-shared: " + this.gotifyApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject object = new JSONObject();
        object.put("to", cid);
        object.put("from", "no-reply@chalmers.it");
        object.put("subject", subject);
        object.put("body", body);

        HttpEntity<JSONObject> entity = new HttpEntity<>(object, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(this.gotifyURL, entity, String.class);

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MailSenderService that = (MailSenderService) o;
        return this.gotifyApiKey.equals(that.gotifyApiKey)
            && this.gotifyURL.equals(that.gotifyURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.gotifyApiKey, this.gotifyURL);
    }

    @Override
    public String toString() {
        return "MailSenderService{"
            + "gotifyApiKey='<redacted>" + '\''
            + ", gotifyURL='" + gotifyURL + '\''
            + '}';
    }
}
