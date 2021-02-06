package it.chalmers.gamma.client.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ClientAddedResponse {
    private final String clientSecret;

    public ClientAddedResponse(String secret) {
        this.clientSecret = secret;
    }

    public String getClientSecret() {
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
