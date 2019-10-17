package it.chalmers.delta.response;

import it.chalmers.delta.db.entity.ApiKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetApiKeyResponse extends ResponseEntity<ApiKey> {
    public GetApiKeyResponse(ApiKey apiKey) {
        super(apiKey, HttpStatus.ACCEPTED);
    }
}
