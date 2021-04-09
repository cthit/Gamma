package it.chalmers.gamma.domain.apikey.service;

import it.chalmers.gamma.domain.apikey.service.ApiKeyName;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.apikey.service.ApiKeyId;
import it.chalmers.gamma.domain.apikey.service.ApiKeyToken;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

public class ApiKeyDTO implements DTO {

    private final ApiKeyId id;
    private final ApiKeyName name;
    private final TextDTO description;
    private final ApiKeyToken key;

    public ApiKeyDTO(ApiKeyName name, TextDTO description, ApiKeyToken key) {
        this(new ApiKeyId(), name, description, key);
    }

    public ApiKeyDTO(ApiKeyId id, ApiKeyName name, TextDTO description, ApiKeyToken key) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.key = key;
    }

    public ApiKeyId getId() {
        return id;
    }

    public ApiKeyName getName() {
        return name;
    }

    public TextDTO getDescription() {
        return description;
    }

    public ApiKeyToken getKey() {
        return this.key;
    }

    @Override
    public String toString() {
        return "ApiKeyDTO{" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", key=" + key +
                '}';
    }
}
