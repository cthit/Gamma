package it.chalmers.gamma.apikey;

import it.chalmers.gamma.apikey.request.CreateApiKeyRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.apikey.response.ApiKeyDeletedResponse;
import it.chalmers.gamma.apikey.response.ApiKeyDoesNotExistResponse;
import it.chalmers.gamma.apikey.response.GetAllAPIKeysResponse;
import it.chalmers.gamma.apikey.response.GetAllAPIKeysResponse.GetAllAPIKeysResponseObject;
import it.chalmers.gamma.apikey.response.GetApiKeyResponse;
import it.chalmers.gamma.apikey.response.GetApiKeyResponse.GetApiKeyResponseObject;
import it.chalmers.gamma.apikey.response.GetApiKeySecretResponse;
import it.chalmers.gamma.apikey.response.GetApiKeySecretResponse.GetApiKeySecretResponseObject;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api_keys")
public class ApiKeyAdminController {
    private final ApiKeyService apiKeyService;
    public ApiKeyAdminController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping()
    public GetApiKeySecretResponseObject createApiKey(
            @Valid @RequestBody CreateApiKeyRequest request,
            BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        ApiKeyDTO apiKeyDTO = this.apiKeyService.createApiKey(requestToDTO(request));
        return new GetApiKeySecretResponse(apiKeyDTO.getKey()).toResponseObject();
    }

    @GetMapping()
    public GetAllAPIKeysResponseObject getAllApiKeys() {
        List<ApiKeyDTO> apiKeys = this.apiKeyService.getAllApiKeys();
        return new GetAllAPIKeysResponse(apiKeys).toResponseObject();
    }

    @GetMapping("/{id}")
    public GetApiKeyResponseObject getApiKey(@PathVariable("id") String id) {
        ApiKeyDTO apiKey = this.apiKeyService.getApiKeyDetails(id);
        return new GetApiKeyResponse(apiKey).toResponseObject();
    }

    @DeleteMapping("/{id}")
    public ApiKeyDeletedResponse deleteApiKeyDetails(@PathVariable("id") String idString) {
        UUID id = UUID.fromString(idString);
        if (!this.apiKeyService.apiKeyExists(id)) {
            throw new ApiKeyDoesNotExistResponse();
        }
        this.apiKeyService.deleteApiKey(id);
        return new ApiKeyDeletedResponse();
    }

    private ApiKeyDTO requestToDTO(CreateApiKeyRequest request) {
        return new ApiKeyDTO(
                request.getName(), request.getDescription()
        );
    }

}
