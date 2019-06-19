package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.ApiKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetApiKeyResponse extends ResponseEntity<ApiKey> {
    public GetApiKeyResponse(ApiKey apiKey) {
        super(apiKey, HttpStatus.ACCEPTED);
    }
}
