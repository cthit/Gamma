package it.chalmers.gamma.domain.client.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.client.data.dto.ClientDTO;

public class GetClientResponse {

    @JsonValue
    public final ClientDTO client;

    public GetClientResponse(ClientDTO client) {
        this.client = client;
    }
}
