package it.chalmers.gamma.domain.apikey.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.chalmers.gamma.domain.text.Text;

import java.util.Objects;
import java.util.UUID;

public class ApiKeyDTO {

    private final UUID id;
    private final String name;
    private final Text description;
    @JsonIgnore
    private final String key;

    public ApiKeyDTO(String name, Text description, String key) {
        this(null, name, description, key);
    }

    public ApiKeyDTO(UUID id, String name, Text description, String key) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.key = key;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    @JsonIgnore
    public String getKey() {
        return this.key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiKeyDTO apiKeyDTO = (ApiKeyDTO) o;
        return Objects.equals(this.id, apiKeyDTO.id)
                && Objects.equals(this.name, apiKeyDTO.name)
                && Objects.equals(this.description, apiKeyDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.name,
                this.description);
    }

    @Override
    public String toString() {
        return "ApiKeyDTO{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", description=" + this.description
                + ", key={<REDACTED>}"
                + '}';
    }
}
