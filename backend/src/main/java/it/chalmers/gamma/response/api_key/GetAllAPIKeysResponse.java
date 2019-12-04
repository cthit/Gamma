package it.chalmers.gamma.response.api_key;

import it.chalmers.gamma.domain.dto.access.ApiKeyDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class GetAllAPIKeysResponse {
    private final List<ApiKeyDTO> apiKeys;


    public GetAllAPIKeysResponse(List<ApiKeyDTO> apiKeys) {
        this.apiKeys = apiKeys;
    }

    public List<ApiKeyDTO> getApiKeys() {
        return apiKeys;
    }

    public GetAllAPIKeysResponseObject getResponseObject() {
        return new GetAllAPIKeysResponseObject(this);
    }

    public static class GetAllAPIKeysResponseObject extends ResponseEntity<GetAllAPIKeysResponse> {

        GetAllAPIKeysResponseObject(GetAllAPIKeysResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
