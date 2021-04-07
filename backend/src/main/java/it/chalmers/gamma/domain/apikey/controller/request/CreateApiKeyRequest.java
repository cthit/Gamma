package it.chalmers.gamma.domain.apikey.controller.request;

import it.chalmers.gamma.domain.apikey.ApiKeyName;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

public class CreateApiKeyRequest {

    public ApiKeyName name;
    public TextDTO description;

}
