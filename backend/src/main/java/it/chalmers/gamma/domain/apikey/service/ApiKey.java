package it.chalmers.gamma.domain.apikey.service;

import it.chalmers.gamma.domain.text.data.dto.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.domain.text.data.db.Text;

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
                this.key
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
                && Objects.equals(this.key, apiKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.name,
                this.description,
                this.key
        );
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
}
