package it.chalmers.gamma.domain.apikey.controller;

import it.chalmers.gamma.domain.apikey.data.ApiKeyDTO;
import it.chalmers.gamma.domain.apikey.exception.ApiKeyNotFoundException;
import it.chalmers.gamma.domain.apikey.service.ApiKeyFinder;
import it.chalmers.gamma.domain.apikey.service.ApiKeyService;
import it.chalmers.gamma.domain.apikey.controller.request.CreateApiKeyRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.apikey.controller.response.ApiKeyDeletedResponse;
import it.chalmers.gamma.domain.apikey.controller.response.ApiKeyDoesNotExistResponse;
import it.chalmers.gamma.domain.apikey.controller.response.GetAllAPIKeysResponse;
import it.chalmers.gamma.domain.apikey.controller.response.GetAllAPIKeysResponse.GetAllAPIKeysResponseObject;
import it.chalmers.gamma.domain.apikey.controller.response.GetApiKeyResponse;
import it.chalmers.gamma.domain.apikey.controller.response.GetApiKeyResponse.GetApiKeyResponseObject;
import it.chalmers.gamma.domain.apikey.controller.response.GetApiKeySecretResponse;
import it.chalmers.gamma.domain.apikey.controller.response.GetApiKeySecretResponse.GetApiKeySecretResponseObject;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;

import it.chalmers.gamma.util.TokenUtils;
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

    private final ApiKeyFinder apiKeyFinder;
    private final ApiKeyService apiKeyService;

    public ApiKeyAdminController(ApiKeyFinder apiKeyFinder, ApiKeyService apiKeyService) {
        this.apiKeyFinder = apiKeyFinder;
        this.apiKeyService = apiKeyService;
    }

    @PostMapping()
    public GetApiKeySecretResponseObject createApiKey(
            @Valid @RequestBody CreateApiKeyRequest request,
            BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        String key = TokenUtils.generateToken(50, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );

        this.apiKeyService.createApiKey(
            new ApiKeyDTO(
                    request.getName(),
                    request.getDescription(),
                    key
            )
        );

        return new GetApiKeySecretResponse(key).toResponseObject();
    }

    @GetMapping()
    public GetAllAPIKeysResponseObject getAllApiKeys() {
        return new GetAllAPIKeysResponse(this.apiKeyFinder.getApiKeys()).toResponseObject();
    }

    @GetMapping("/{id}")
    public GetApiKeyResponseObject getApiKey(@PathVariable("id") UUID apiKeyId) {
        try {
            return new GetApiKeyResponse(this.apiKeyFinder.getApiKey(apiKeyId)).toResponseObject();
        } catch (ApiKeyNotFoundException e) {
            throw new ApiKeyDoesNotExistResponse();
        }
    }

    @DeleteMapping("/{id}")
    public ApiKeyDeletedResponse deleteApiKeyDetails(@PathVariable("id") UUID apiKeyId) {
        try {
            this.apiKeyService.deleteApiKey(apiKeyId);
            return new ApiKeyDeletedResponse();
        } catch (ApiKeyNotFoundException e) {
            throw new ApiKeyDoesNotExistResponse();
        }
    }

}
