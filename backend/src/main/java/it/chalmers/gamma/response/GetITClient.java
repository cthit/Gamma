package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.ITClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITClient extends ResponseEntity<ITClient> {
    public GetITClient(ITClient client) {
        super(client, HttpStatus.ACCEPTED);
    }
}
