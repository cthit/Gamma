package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

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

    @JsonIgnore
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        ApiKey apiKey = (ApiKey) o;
        return Objects.equals(id, apiKey.id) &&
                Objects.equals(name, apiKey.name) &&
                Objects.equals(description, apiKey.description) &&
                Objects.equals(key, apiKey.key) &&
                Objects.equals(createdAt, apiKey.createdAt) &&
                Objects.equals(lastModifiedAt, apiKey.lastModifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, key, createdAt, lastModifiedAt);
    }

    @Override
    public String toString() {
        return "ApiKey{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description=" + description +
                ", key='" + key + '\'' +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
                '}';
    }
}
