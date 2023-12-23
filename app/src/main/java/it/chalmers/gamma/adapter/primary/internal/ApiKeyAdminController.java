package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.apikey.ApiKeyFacade;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/api-keys")
public final class ApiKeyAdminController {

    private final ApiKeyFacade apiKeyFacade;

    public ApiKeyAdminController(ApiKeyFacade apiKeyFacade) {
        this.apiKeyFacade = apiKeyFacade;
    }

    @PostMapping()
    public String createApiKey(@RequestBody CreateApiKeyRequest request) {
        return this.apiKeyFacade.create(
                new ApiKeyFacade.NewApiKey(
                        request.prettyName,
                        request.svDescription,
                        request.enDescription,
                        request.keyType
                )
        );
    }

    @PostMapping("/{id}/reset")
    public String resetApiKey(@PathVariable("id") UUID id) {
        String token;

        try {
            token = this.apiKeyFacade.resetApiKeyToken(id);
        } catch (ApiKeyFacade.ApiKeyNotFoundException e) {
            throw new ApiKeyNotFoundResponse();
        }

        return token;
    }

    @GetMapping()
    public List<ApiKeyFacade.ApiKeyDTO> getAllApiKeys() {
        return this.apiKeyFacade.getAll();
    }

    @GetMapping("/types")
    public String[] getTypes() {
        return this.apiKeyFacade.getApiKeyTypes();
    }

    @GetMapping("/{id}")
    public ApiKeyFacade.ApiKeyDTO getApiKey(@PathVariable("id") String id) {
        return this.apiKeyFacade.getById(UUID.fromString(id))
                .orElseThrow(ApiKeyNotFoundResponse::new);
    }

    @DeleteMapping("/{id}")
    public ApiKeyDeletedResponse deleteApiKey(@PathVariable("id") UUID apiKeyId) {
        try {
            this.apiKeyFacade.delete(apiKeyId);
            return new ApiKeyDeletedResponse();
        } catch (ApiKeyFacade.ApiKeyNotFoundException e) {
            throw new ApiKeyNotFoundResponse();
        }
    }

    private record CreateApiKeyRequest(
            String prettyName,
            String svDescription,
            String enDescription,
            String keyType // goldapps, info, allowlist
    ) {
    }

    private static class ApiKeyDeletedResponse extends SuccessResponse {
    }

    private static class ApiKeyNotFoundResponse extends NotFoundResponse {
    }

}
