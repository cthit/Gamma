package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.internal.text.data.db.Text;

import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name = "apikey")
public class ApiKey extends MutableEntity<ApiKeyDTO> {

    @EmbeddedId
    private ApiKeyId id;

    @Embedded
    private ApiKeyToken key;

    @JoinColumn(name = "description")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text description;

    @Embedded
    private ApiKeyName name;

    @Enumerated(EnumType.STRING)
    private ApiKeyType keyType;

    protected ApiKey() { }

    protected ApiKey(ApiKeyDTO apiKey) {
        assert(apiKey.id() != null);
        assert(apiKey.key() != null);

        this.id = apiKey.id();
        this.key = apiKey.key();

        this.description = new Text();

        apply(apiKey);
    }

    @Override
    protected void apply(ApiKeyDTO ak){
        assert(this.id == ak.id());

        this.name = ak.name();
        this.keyType = ak.keyType();

        if(this.description == null) {
            this.description = new Text();
        }

        this.description.apply(ak.description());
    }

    @Override
    protected ApiKeyDTO toDTO() {
        return new ApiKeyDTO(
                this.id,
                this.name,
                this.description.toDTO(),
                this.key,
                this.keyType
        );
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
                && Objects.equals(this.keyType, apiKey.keyType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.name,
                this.description,
                this.key,
                this.keyType
        );
    }

    @Override
    public String toString() {
        return "ApiKey{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", description=" + this.description
                + ", key='" + this.key + '\''
                + ", keyType='" + this.keyType + '\''
                + '}';
    }
}
