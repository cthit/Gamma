package it.chalmers.gamma.domain.apikey.data;

import it.chalmers.gamma.domain.apikey.ApiKeyId;
import it.chalmers.gamma.domain.text.Text;

import java.util.UUID;

public class ApiKeyInformationDTO {

    private final ApiKeyId id;
    private final String name;
    private final Text description;

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

    public Text getDescription() {
        return description;
    }
}
