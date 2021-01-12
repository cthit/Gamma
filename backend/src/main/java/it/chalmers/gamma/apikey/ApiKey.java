package it.chalmers.gamma.apikey;

import it.chalmers.gamma.domain.text.Text;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "apikey")
public class ApiKey {
    @Id
    @Column(updatable = false)
    private UUID id;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @JoinColumn(name = "description")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text description;

    @Column(name = "key", length = 150, nullable = false)
    private String key;

    @Column(name = "created_at", nullable = false)
    @ColumnDefault("current_timestamp")
    private Instant createdAt;

    @Column(name = "last_modified_at", nullable = false)
    @ColumnDefault("current_timestamp")
    private Instant lastModifiedAt;

    public ApiKey() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.lastModifiedAt = Instant.now();
    }

    public ApiKey(String name, String key, Text description) {
        this();
        this.name = name;
        this.key = key;
        this.description = description;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Text getDescription() {
        return this.description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    public void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public ApiKeyDTO toDTO() {
        return new ApiKeyDTO(this.id, this.name, this.description, this.createdAt, this.lastModifiedAt, this.key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiKey apiKey = (ApiKey) o;
        return Objects.equals(this.id, apiKey.id)
                && Objects.equals(this.name, apiKey.name)
                && Objects.equals(this.description, apiKey.description)
                && Objects.equals(this.key, apiKey.key)
                && Objects.equals(this.createdAt, apiKey.createdAt)
                && Objects.equals(this.lastModifiedAt, apiKey.lastModifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.name,
                this.description,
                this.key,
                this.createdAt,
                this.lastModifiedAt);
    }

    @Override
    public String toString() {
        return "ApiKey{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", description=" + this.description
                + ", key='" + this.key + '\''
                + ", createdAt=" + this.createdAt
                + ", lastModifiedAt=" + this.lastModifiedAt
                + '}';
    }
}
