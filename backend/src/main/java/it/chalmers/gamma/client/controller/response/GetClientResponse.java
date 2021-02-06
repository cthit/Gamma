package it.chalmers.gamma.client.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.client.dto.ClientDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetClientResponse {
    @JsonUnwrapped
    private final ClientDTO itClient;

    public GetClientResponse(ClientDTO itClient) {
        this.itClient = itClient;
    }

    public ClientDTO getItClient() {
        return this.itClient;
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
