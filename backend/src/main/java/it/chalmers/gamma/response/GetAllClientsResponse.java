package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.ITClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetAllClientsResponse extends ResponseEntity<List<ITClient>> {
    public GetAllClientsResponse(List<ITClient> clients) {
        super(clients, HttpStatus.ACCEPTED);
    }
}
