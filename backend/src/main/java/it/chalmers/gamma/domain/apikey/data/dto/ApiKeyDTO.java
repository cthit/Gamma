package it.chalmers.gamma.domain.apikey.data.dto;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.apikey.domain.ApiKeyId;
import it.chalmers.gamma.domain.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

public class ApiKeyDTO implements DTO {

    private final ApiKeyId id;
    private final String name;
    private final TextDTO description;
    private final ApiKeyToken key;

    public ApiKeyDTO(String name, TextDTO description, ApiKeyToken key) {
        this(new ApiKeyId(), name, description, key);
    }

    public ApiKeyDTO(ApiKeyId id, String name, TextDTO description, ApiKeyToken key) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.key = key;
    }

    public ApiKeyId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TextDTO getDescription() {
        return description;
    }

    public ApiKeyToken getKey() {
        return this.key;
    }

}
