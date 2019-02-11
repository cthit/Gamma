package it.chalmers.gamma.util;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class ImageITUtils {
    //@Value()
    private static int imageITPort;        // TODO Add Env Variable
    private static String imageITURL;      // TODO Add Env Variable
    private static String apiKey;

    public static String getImage(UUID imageId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "");
        HttpEntity<JSONObject> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
       // ResponseEntity<String> res = restTemplate.getForEntity(imageITURL + ":" + imageITPort, entity, UUID.class);
        return null;
    }

    public static UUID saveImaeg(){
        HttpHeaders headers = new HttpHeaders();
        return null;
    }
}
