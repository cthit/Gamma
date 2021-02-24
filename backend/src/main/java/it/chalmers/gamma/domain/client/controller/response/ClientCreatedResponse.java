package it.chalmers.gamma.domain.client.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.client.domain.ClientSecret;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ClientCreatedResponse {

    @JsonValue
    public final ClientSecret clientSecret;

    public ClientCreatedResponse(ClientSecret secret) {
        this.clientSecret = secret;
    }

}
