package it.chalmers.gamma.service;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class MailSenderService{

    @Value("${application.gotify.key}")
    private String gotifyApiKey;

    @Value("${application.gotify.url}")
    private String gotifyURL;

    public MailSenderService() {

    }

    /**
     * Sends mail using Gotify Rest API, see https://github.com/cthit/gotify
     * @return true if message was successfully sent false if not
     */
    public boolean sendMail(String cid, String subject, String body){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "pre-shared: " + gotifyApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject object = new JSONObject();
        object.put("to", cid);
        object.put("from", "no-reply@chalmers.it");
        object.put("subject", subject);
        object.put("body",body);

        HttpEntity<JSONObject> entity = new HttpEntity<>(object, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(gotifyURL, entity, String.class);

        System.out.println(response.toString());

        return true;
    }
}
