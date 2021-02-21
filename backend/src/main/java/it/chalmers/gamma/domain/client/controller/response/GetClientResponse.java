package it.chalmers.gamma.domain.client.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.client.data.ClientDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetClientResponse {

    @JsonValue
    private final ClientDTO client;

    public GetClientResponse(ClientDTO client) {
        this.client = client;
    }

    public GetClientResponseObject toResponseObject() {
        return new GetClientResponseObject(this);
    }

    public static class GetClientResponseObject extends ResponseEntity<GetClientResponse> {
        GetClientResponseObject(GetClientResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
