package it.chalmers.gamma.domain.client.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.client.service.ClientDTO;

public class GetClientResponse {

    @JsonValue
    protected final ClientDTO client;

    protected GetClientResponse(ClientDTO client) {
        this.client = client;
    }
}
