package it.chalmers.gamma.response.apikey;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetApiKeySecretResponse {

    @JsonUnwrapped
    private final String secret;

    public GetApiKeySecretResponse(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return this.secret;
    }

    public GetApiKeySecretResponseObject toResponseObject() {
        return new GetApiKeySecretResponseObject(this);
    }

    public static class GetApiKeySecretResponseObject extends ResponseEntity<GetApiKeySecretResponse> {
        GetApiKeySecretResponseObject(GetApiKeySecretResponse response) {
            super(response, HttpStatus.OK);
        }
    }

}
