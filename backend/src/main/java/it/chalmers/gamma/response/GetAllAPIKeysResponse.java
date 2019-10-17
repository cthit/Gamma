package it.chalmers.delta.response;

import it.chalmers.delta.db.entity.ApiKey;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;



public class GetAllAPIKeysResponse extends ResponseEntity<List<ApiKey>> {

    public GetAllAPIKeysResponse(List<ApiKey> apiKeys) {
        super(apiKeys, HttpStatus.ACCEPTED);
    }
}
