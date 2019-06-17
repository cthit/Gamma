package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.ApiKey;
import it.chalmers.gamma.requests.CreateApiKeyRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.ApiKeyService;
import it.chalmers.gamma.util.InputValidationUtils;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/api_keys")
public class ApiKeyAdminController {
    private final ApiKeyService apiKeyService;
    public ApiKeyAdminController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public JSONObject createApiKey(@Valid @RequestBody CreateApiKeyRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        String apiKey = apiKeyService.CreateApiKey(request);
        JSONObject response = new JSONObject();
        response.put("apiKey", apiKey);
        return response;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ApiKey>> getAllApiKeys() {
        List<ApiKey> apiKeys = apiKeyService.getAllApiKeys();
        return new GetAllAPIKeysResponse(apiKeys);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ApiKey> getApiKey(@PathVariable("id") String id) {
        ApiKey apiKey = this.apiKeyService.getApiKeyDetails(UUID.fromString(id));
        if(apiKey == null) {
            apiKey = this.apiKeyService.getApiKeyDetails(id);
        }
        if(apiKey == null) {
            throw new ApiKeyDoesNotExistResponse();
        }
        return new GetApiKeyResponse(apiKey);
    }

    @RequestMapping(value = "/{id}")
    public ResponseEntity<String> deleteApiKeyDetails(@PathVariable("id") String idString) {
        UUID id = UUID.fromString(idString);
        if(!this.apiKeyService.apiKeyExists(id)) {
            throw new ApiKeyDoesNotExistResponse();
        }
        this.apiKeyService.deleteApiKey(id);
        return new ApiKeyDeletedResponse();
    }

}
