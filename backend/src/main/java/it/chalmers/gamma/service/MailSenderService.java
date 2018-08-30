package it.chalmers.gamma.service;

import com.google.api.client.json.Json;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.util.JSONPObject;
import org.json.simple.JSONObject;
import org.mortbay.util.ajax.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@PropertySource("classpath:development.properties")
@PropertySource("classpath:secrets.properties")
@Service
public class MailSenderService{

    @Value("${email.password}")
    private String password;

    @Value("${email.username}")
    private String mail;

    @Value("${email.base_url}")
    private String baseUrl;

    @Value("client_email")
    private String clientEmail;

    @Value("${gotify.key}")
    private String gotifyApiKey;

    @Value("${gotify.url}")
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

        HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(object, headers);
        RestTemplate restTemplate = new RestTemplate();


        System.out.println(mail);
        System.out.println(baseUrl);
        ResponseEntity<String> response = restTemplate.postForEntity(gotifyURL, entity, String.class);

        System.out.println(response.toString());

        return true;
    }
}
