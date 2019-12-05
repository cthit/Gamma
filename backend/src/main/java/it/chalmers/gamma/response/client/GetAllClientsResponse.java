package it.chalmers.gamma.response.client;

import it.chalmers.gamma.domain.dto.access.ITClientDTO;
import it.chalmers.gamma.response.user.GetAllITUsersResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllClientsResponse {
   private final List<ITClientDTO> clients;

    public GetAllClientsResponse(List<ITClientDTO> clients) {
        this.clients = clients;
    }

    public List<ITClientDTO> getClients() {
        return clients;
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
