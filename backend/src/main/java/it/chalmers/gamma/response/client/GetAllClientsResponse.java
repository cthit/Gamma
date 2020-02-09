package it.chalmers.gamma.response.client;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.dto.access.ITClientDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllClientsResponse {
    @JsonValue
    private final List<ITClientDTO> clients;

    public GetAllClientsResponse(List<ITClientDTO> clients) {
        this.clients = clients;
    }

    public List<ITClientDTO> getClients() {
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
