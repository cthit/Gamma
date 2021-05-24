package it.chalmers.gamma.internal.apikey.controller;

import it.chalmers.gamma.domain.Name;
import it.chalmers.gamma.internal.apikey.service.ApiKeyInformationDTO;
import it.chalmers.gamma.domain.ApiKeyType;
import it.chalmers.gamma.internal.text.service.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.ApiKeyId;
import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.internal.apikey.service.ApiKeyDTO;
import it.chalmers.gamma.internal.apikey.service.ApiKeyFinder;
import it.chalmers.gamma.internal.apikey.service.ApiKeyService;

import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/api_keys")
public class ApiKeyAdminController {

    private final ApiKeyFinder apiKeyFinder;
    private final ApiKeyService apiKeyService;

    public ApiKeyAdminController(ApiKeyFinder apiKeyFinder, ApiKeyService apiKeyService) {
        this.apiKeyFinder = apiKeyFinder;
        this.apiKeyService = apiKeyService;
    }

    private record CreateApiKeyRequest(Name name, TextDTO description, ApiKeyType keyType) { }

    @PostMapping()
    public ApiKeyToken createApiKey(@RequestBody CreateApiKeyRequest request) {
        ApiKeyToken key = new ApiKeyToken();

        this.apiKeyService.create(
            new ApiKeyDTO(
                    new ApiKeyId(),
                    request.name,
                    request.description,
                    key,
                    request.keyType
            )
        );

        return key;
    }

    @GetMapping()
    public List<ApiKeyInformationDTO> getAllApiKeys() {
        return this.apiKeyFinder.getAll();
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

    private static class ApiKeyDeletedResponse extends SuccessResponse { }

    private static class ApiKeyNotFoundResponse extends ErrorResponse {
        private ApiKeyNotFoundResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
