package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.apikey.ApiKeyRepository;
import it.chalmers.gamma.app.domain.ApiKeyType;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.app.domain.Text;
import it.chalmers.gamma.app.domain.ApiKeyId;
import it.chalmers.gamma.app.domain.ApiKeyToken;
import it.chalmers.gamma.app.domain.ApiKey;

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
public class ApiKeyAdminController {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyAdminController(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    private record CreateApiKeyRequest(PrettyName prettyName, Text description, ApiKeyType keyType) { }

    @PostMapping()
    public ApiKeyToken createApiKey(@RequestBody CreateApiKeyRequest request) {
        ApiKeyToken key = ApiKeyToken.generate();

        this.apiKeyRepository.create(
            new ApiKey(
                    ApiKeyId.generate(),
                    request.prettyName,
                    request.description,
                    request.keyType,
                    key
            )
        );

        return key;
    }

    @PostMapping("/{id}/reset")
    public ApiKeyToken resetApiKey(@PathVariable("id") ApiKeyId id) {
        ApiKeyToken token;

        try {
            token = this.apiKeyRepository.generateNewToken(id);
        } catch (ApiKeyRepository.ApiKeyNotFoundException e) {
            throw new ApiKeyNotFoundResponse();
        }

        return token;
    }

    @GetMapping()
    public List<ApiKey> getAllApiKeys() {
        return this.apiKeyRepository.getAll();
    }

    @GetMapping("/types")
    public ApiKeyType[] getTypes() {
        return ApiKeyType.values();
    }

    @GetMapping("/{id}")
    public ApiKey getApiKey(@PathVariable("id") ApiKeyId id) {
        return this.apiKeyRepository.getById(id)
                .orElseThrow(ApiKeyNotFoundResponse::new);
    }

    @DeleteMapping("/{id}")
    public ApiKeyDeletedResponse deleteApiKey(@PathVariable("id") ApiKeyId apiKeyId) {
        try {
            this.apiKeyRepository.delete(apiKeyId);
            return new ApiKeyDeletedResponse();
        } catch (ApiKeyRepository.ApiKeyNotFoundException e) {
            throw new ApiKeyNotFoundResponse();
        }
    }

    private static class ApiKeyDeletedResponse extends SuccessResponse { }

    private static class ApiKeyNotFoundResponse extends NotFoundResponse { }

}
