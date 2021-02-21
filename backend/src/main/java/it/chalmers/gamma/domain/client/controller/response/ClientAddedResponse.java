package it.chalmers.gamma.domain.client.controller.response;

import it.chalmers.gamma.domain.client.ClientSecret;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ClientAddedResponse {

    private final ClientSecret clientSecret;

    public ClientAddedResponse(ClientSecret secret) {
        this.clientSecret = secret;
    }

    public ClientSecret getClientSecret() {
        return this.clientSecret;
    }

    public ClientAddedResponseObject toResponseObject() {
        return new ClientAddedResponseObject(this);
    }

    public static class ClientAddedResponseObject extends ResponseEntity<ClientAddedResponse> {
        ClientAddedResponseObject(ClientAddedResponse body) {
            super(body, HttpStatus.ACCEPTED);
        }
    }
}
