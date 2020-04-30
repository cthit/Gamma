package it.chalmers.gamma.domain.dto.access;

import it.chalmers.gamma.db.entity.Text;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ApiKeyDTO {
    private final UUID id;
    private final String name;
    private final Text description;
    private final Instant createdAt;
    private final Instant lastModifiedAt;
    private final String key;

    public ApiKeyDTO(UUID id, String name, Text description, Instant createdAt, Instant lastModifiedAt, String key) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.key = key;
    }

    public ApiKeyDTO(String name, Text description) {
        this(null, name, description, null, null, "");
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Text getDescription() {
        return this.description;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    public String getKey() {
        return key;
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
                && Objects.equals(this.description, apiKeyDTO.description)
                && Objects.equals(this.createdAt, apiKeyDTO.createdAt)
                && Objects.equals(this.lastModifiedAt, apiKeyDTO.lastModifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.name,
                this.description,
                this.createdAt,
                this.lastModifiedAt);
    }

    @Override
    public String toString() {
        return "ApiKeyDTO{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", description=" + this.description
                + ", createdAt=" + this.createdAt
                + ", lastModifiedAt=" + this.lastModifiedAt
                + ", key={secret}"
                + '}';
    }
}
