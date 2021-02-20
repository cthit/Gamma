package it.chalmers.gamma.domain.apikey.data;

import it.chalmers.gamma.domain.GEntity;
import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.apikey.ApiKeyId;
import it.chalmers.gamma.domain.text.Text;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "apikey")
public class ApiKey implements GEntity<ApiKeyDTO> {

    @EmbeddedId
    private ApiKeyId id;

    @Column(name = "name")
    private String name;

    @JoinColumn(name = "description")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text description;

    @Column(name = "key")
    private String key;

    protected ApiKey() { }

    public ApiKey(ApiKeyDTO apiKey) {
        try {
            apply(apiKey);
        } catch (IDsNotMatchingException ignored) { }

        if(this.id == null) {
            this.id = new ApiKeyId();
        }

    }

    public ApiKeyId getId() {
        return this.id;
    }

    public void setId(ApiKeyId id) {
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
                && Objects.equals(this.key, apiKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.name,
                this.description,
                this.key);
    }

    @Override
    public String toString() {
        return "ApiKey{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", description=" + this.description
                + ", key='" + this.key + '\''
                + '}';
    }

    @Override
    public void apply(ApiKeyDTO ak) throws IDsNotMatchingException {
        if(this.id != null && this.id != ak.getId()) {
            throw new IDsNotMatchingException();
        }

        this.id = ak.getId();
        this.name = ak.getName();
        this.key = ak.getKey();
        this.description = ak.getDescription();
    }
}
