package it.chalmers.gamma.response.client;

import it.chalmers.gamma.domain.dto.access.ITClientDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITClientResponse {
    private final ITClientDTO itClient;

    public GetITClientResponse(ITClientDTO itClient) {
        this.itClient = itClient;
    }

    public ITClientDTO getItClient() {
        return itClient;
    }

    public GetITClientResponseObject getResponseObject() {
        return new GetITClientResponseObject(this);
    }

    public static class GetITClientResponseObject extends ResponseEntity<GetITClientResponse> {
        GetITClientResponseObject(GetITClientResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
