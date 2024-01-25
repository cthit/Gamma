package it.chalmers.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoClientApplication.class, args);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "pre-shared INFO-super-secret-code");
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange("http://gamma:8081/api/info/v1/groups", HttpMethod.GET, requestEntity, String.class);
		System.out.println("Gamma responded with " + response.getHeaders() + response.getBody());


		HttpHeaders headers2 = new HttpHeaders();
		headers2.add(HttpHeaders.AUTHORIZATION, "pre-shared test-api-key-secret-code");
		headers2.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> requestEntity2 = new HttpEntity<>(headers2);

		RestTemplate restTemplate2 = new RestTemplate();
		try{
			ResponseEntity<String> response2 = restTemplate2.exchange("http://gamma:8081/api/client/v1/users", HttpMethod.GET, requestEntity2, String.class);
			System.out.println("Gamma responded with " + response2.getHeaders() + response2.getBody());
		}catch(Exception e) {
			e.printStackTrace();
		}


	}


}
