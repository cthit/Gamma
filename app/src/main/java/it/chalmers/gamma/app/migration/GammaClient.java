package it.chalmers.gamma.app.migration;

import java.util.Collections;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class GammaClient {

  private static final String URL_PREFIX = "https://gamma.chalmers.it/api";
  private final RestTemplate restTemplate;
  private final HttpHeaders headers;

  public GammaClient(String token) {
    this.restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "pre-shared " + token);
    this.headers = headers;
  }

  public <T> T get(String url, Class<T> responseType) {
    try {
      HttpEntity<String> entity = new HttpEntity<>("parameters", this.headers);
      ResponseEntity<T> responseEntity =
          restTemplate.exchange(URL_PREFIX + url, HttpMethod.GET, entity, responseType);
      return responseEntity.getBody();
    } catch (HttpStatusCodeException e) {
      throw new RuntimeException(
          "Failed to retrieve data from API. Error: " + e.getResponseBodyAsString(), e);
    }
  }

  public <T> List<T> getList(String url, ParameterizedTypeReference<List<T>> responseType) {
    try {
      HttpEntity<String> entity = new HttpEntity<>("parameters", this.headers);
      ResponseEntity<List<T>> responseEntity =
          restTemplate.exchange(URL_PREFIX + url, HttpMethod.GET, entity, responseType);
      return responseEntity.getBody();
    } catch (HttpStatusCodeException e) {
      throw new RuntimeException(
          "Failed to retrieve data from API. Error: " + e.getResponseBodyAsString(), e);
    }
  }
}
