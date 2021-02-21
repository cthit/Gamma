package it.chalmers.gamma.domain.client.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.client.data.ClientDTO;
import it.chalmers.gamma.domain.client.data.ClientDetailsDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllClientsResponse {

    @JsonValue
    private final List<ClientDTO> clients;

    public GetAllClientsResponse(List<ClientDTO> clients) {
        this.clients = clients;
    }

    public List<ClientDTO> getClients() {
        return this.clients;
    }

    public GetAllClientResponseObject toResponseObject() {
        return new GetAllClientResponseObject(this);
    }

    public static class GetAllClientResponseObject extends ResponseEntity<GetAllClientsResponse> {
        GetAllClientResponseObject(GetAllClientsResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
