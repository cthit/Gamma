package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.apikey.ApiKeyFacade;
import it.chalmers.gamma.app.apikey.ApiKeyRepository;
import it.chalmers.gamma.domain.apikey.ApiKeyType;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.common.Text;
import it.chalmers.gamma.domain.apikey.ApiKeyId;
import it.chalmers.gamma.domain.apikey.ApiKeyToken;
import it.chalmers.gamma.domain.apikey.ApiKey;

import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/api_keys")
public final class ApiKeyAdminController {

    private final ApiKeyFacade apiKeyFacade;

    public ApiKeyAdminController(ApiKeyFacade apiKeyFacade) {
        this.apiKeyFacade = apiKeyFacade;
    }

    private record CreateApiKeyRequest(PrettyName prettyName, Text description, ApiKeyType keyType) { }

    @PostMapping()
    public ApiKeyToken createApiKey(@RequestBody CreateApiKeyRequest request) {
        return null;
//        return this.apiKeyFacade.create(ApiKey.create(request.prettyName, request.description, request.keyType));
    }

    @PostMapping("/{id}/reset")
    public ApiKeyToken resetApiKey(@PathVariable("id") ApiKeyId id) {
        ApiKeyToken token;

        try {
            token = this.apiKeyFacade.resetApiKeyToken(id);
        } catch (ApiKeyRepository.ApiKeyNotFoundException e) {
            throw new ApiKeyNotFoundResponse();
        }

        return token;
    }

    @GetMapping()
    public List<ApiKey> getAllApiKeys() {
        return this.apiKeyFacade.getAll();
    }

    @GetMapping("/types")
    public ApiKeyType[] getTypes() {
        return ApiKeyType.values();
    }

    @GetMapping("/{id}")
    public ApiKey getApiKey(@PathVariable("id") ApiKeyId id) {
        return this.apiKeyFacade.get(id)
                .orElseThrow(ApiKeyNotFoundResponse::new);
    }

    @DeleteMapping("/{id}")
    public ApiKeyDeletedResponse deleteApiKey(@PathVariable("id") ApiKeyId apiKeyId) {
        try {
            this.apiKeyFacade.delete(apiKeyId);
            return new ApiKeyDeletedResponse();
        } catch (ApiKeyRepository.ApiKeyNotFoundException e) {
            throw new ApiKeyNotFoundResponse();
        }
    }

    private static class ApiKeyDeletedResponse extends SuccessResponse { }

    private static class ApiKeyNotFoundResponse extends NotFoundResponse { }

}
