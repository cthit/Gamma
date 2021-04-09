package it.chalmers.gamma.domain.client.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.client.service.ClientSecret;

public class ClientCreatedResponse {

    @JsonValue
    protected final ClientSecret clientSecret;

    protected ClientCreatedResponse(ClientSecret secret) {
        this.clientSecret = secret;
    }

}
