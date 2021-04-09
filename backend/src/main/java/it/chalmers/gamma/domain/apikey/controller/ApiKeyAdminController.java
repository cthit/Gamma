package it.chalmers.gamma.domain.apikey.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.apikey.service.ApiKeyId;
import it.chalmers.gamma.domain.apikey.service.ApiKeyToken;
import it.chalmers.gamma.domain.apikey.service.ApiKeyDTO;
import it.chalmers.gamma.domain.apikey.service.ApiKeyFinder;
import it.chalmers.gamma.domain.apikey.service.ApiKeyService;

import it.chalmers.gamma.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
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

    private final ApiKeyFinder apiKeyFinder;
    private final ApiKeyService apiKeyService;

    public ApiKeyAdminController(ApiKeyFinder apiKeyFinder, ApiKeyService apiKeyService) {
        this.apiKeyFinder = apiKeyFinder;
        this.apiKeyService = apiKeyService;
    }

    @PostMapping()
    public GetApiKeySecretResponse createApiKey(@RequestBody CreateApiKeyRequest request) {
        ApiKeyToken key = new ApiKeyToken();

        this.apiKeyService.create(
            new ApiKeyDTO(
                    request.name,
                    request.description,
                    key
            )
        );

        return new GetApiKeySecretResponse(key);
    }

    @GetMapping()
    public ResponseEntity<GetAllApiKeyInformationResponse> getAllApiKeys() {
        return ResponseUtils.toResponseObject(new GetAllApiKeyInformationResponse(this.apiKeyFinder.getAll()));
    }

    @DeleteMapping("/{id}")
    public ApiKeyDeletedResponse deleteApiKey(@PathVariable("id") ApiKeyId apiKeyId) {
        try {
            this.apiKeyService.delete(apiKeyId);
            return new ApiKeyDeletedResponse();
        } catch (EntityNotFoundException e) {
            throw new ApiKeyNotFoundResponse();
        }
    }

}
