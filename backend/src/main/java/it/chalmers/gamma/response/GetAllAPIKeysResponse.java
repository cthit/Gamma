package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.ApiKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetAllAPIKeysResponse extends ResponseEntity<List<ApiKey>> {

    public GetAllAPIKeysResponse(List<ApiKey> apiKeys) {
        super(apiKeys, HttpStatus.ACCEPTED);
    }
}
