package it.chalmers.gamma.domain.apikey.controller;

import it.chalmers.gamma.domain.apikey.service.ApiKeyName;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

public class CreateApiKeyRequest {

    protected ApiKeyName name;
    protected TextDTO description;

    protected CreateApiKeyRequest() { }

}
