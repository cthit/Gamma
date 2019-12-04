package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.ApiKey;
import it.chalmers.gamma.domain.dto.access.ApiKeyDTO;
import it.chalmers.gamma.requests.CreateApiKeyRequest;
import it.chalmers.gamma.response.api_key.ApiKeyDeletedResponse;
import it.chalmers.gamma.response.api_key.ApiKeyDoesNotExistResponse;
import it.chalmers.gamma.response.api_key.GetAllAPIKeysResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.api_key.GetAllAPIKeysResponse.GetAllAPIKeysResponseObject;
import it.chalmers.gamma.response.api_key.GetApiKeyResponse;
import it.chalmers.gamma.response.api_key.GetApiKeyResponse.GetApiKeyResponseObject;
import it.chalmers.gamma.service.ApiKeyService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api_keys")
public class ApiKeyAdminController {
    private final ApiKeyService apiKeyService;
    public ApiKeyAdminController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public GetApiKeyResponseObject createApiKey(@Valid @RequestBody CreateApiKeyRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        ApiKeyDTO apiKey = this.apiKeyService.createApiKey(requestToDTO(request));
        return new GetApiKeyResponse(apiKey).getResponseObject();
    }

    @RequestMapping(method = RequestMethod.GET)
    public GetAllAPIKeysResponseObject getAllApiKeys() {
        List<ApiKeyDTO> apiKeys = this.apiKeyService.getAllApiKeys();
        return new GetAllAPIKeysResponse(apiKeys).getResponseObject();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public GetApiKeyResponseObject getApiKey(@PathVariable("id") String id) {
        ApiKeyDTO apiKey = this.apiKeyService.getApiKeyDetails(id);
        return new GetApiKeyResponse(apiKey).getResponseObject();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiKeyDeletedResponse deleteApiKeyDetails(@PathVariable("id") String idString) {
        UUID id = UUID.fromString(idString);
        if (!this.apiKeyService.apiKeyExists(id)) {
            throw new ApiKeyDoesNotExistResponse();
        }
        this.apiKeyService.deleteApiKey(id);
        return new ApiKeyDeletedResponse();
    }

    ApiKeyDTO requestToDTO(CreateApiKeyRequest request) {
        return new ApiKeyDTO(
                request.getName(), request.getDescription()
        );
    }

}
