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

    public ApiKeyDTO(UUID id, String name, Text description, Instant createdAt, Instant lastModifiedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
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
                + "id=" + id
                + ", name='" + name + '\''
                + ", description=" + description
                + ", createdAt=" + createdAt
                + ", lastModifiedAt=" + lastModifiedAt
                + '}';
    }
}
