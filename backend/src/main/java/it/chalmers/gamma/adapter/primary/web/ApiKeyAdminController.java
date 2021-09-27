package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.facade.ApiKeyFacade;
import it.chalmers.gamma.app.port.repository.ApiKeyRepository;

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
import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/api_keys")
public final class ApiKeyAdminController {

    private final ApiKeyFacade apiKeyFacade;

    public ApiKeyAdminController(ApiKeyFacade apiKeyFacade) {
        this.apiKeyFacade = apiKeyFacade;
    }

    private record CreateApiKeyRequest(
            String prettyName,
            String svText,
            String enText,
            String keyType //client, goldapps, chalmersit
    ) { }

    @PostMapping()
    public String createApiKey(@RequestBody CreateApiKeyRequest request) {
        return this.apiKeyFacade.create(
                new ApiKeyFacade.NewApiKey(
                        request.prettyName,
                        request.svText,
                        request.enText,
                        request.keyType
                )
        );
    }

    @PostMapping("/{id}/reset")
    public String resetApiKey(@PathVariable("id") UUID id) {
        String token;

        try {
            token = this.apiKeyFacade.resetApiKeyToken(id);
        } catch (ApiKeyRepository.ApiKeyNotFoundException e) {
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
        return this.apiKeyFacade.getById(id)
                .orElseThrow(ApiKeyNotFoundResponse::new);
    }

    @DeleteMapping("/{id}")
    public ApiKeyDeletedResponse deleteApiKey(@PathVariable("id") UUID apiKeyId) {
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
