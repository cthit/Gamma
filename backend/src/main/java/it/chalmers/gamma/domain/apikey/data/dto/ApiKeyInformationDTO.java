package it.chalmers.gamma.domain.apikey.data.dto;

import it.chalmers.gamma.domain.DTO;
import it.chalmers.gamma.domain.apikey.domain.ApiKeyId;
import it.chalmers.gamma.domain.text.data.db.Text;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

public class ApiKeyInformationDTO implements DTO {

    private final ApiKeyId id;
    private final String name;
    private final TextDTO description;

    public ApiKeyInformationDTO(ApiKeyId id, String name, TextDTO description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public ApiKeyInformationDTO(ApiKeyDTO apiKey) {
        this.id = apiKey.getId();
        this.name = apiKey.getName();
        this.description = apiKey.getDescription();
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
}
