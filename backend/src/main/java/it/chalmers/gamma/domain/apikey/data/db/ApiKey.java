package it.chalmers.gamma.domain.apikey.data.db;

import it.chalmers.gamma.domain.apikey.ApiKeyName;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.domain.apikey.domain.ApiKeyId;
import it.chalmers.gamma.domain.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.domain.apikey.data.dto.ApiKeyDTO;
import it.chalmers.gamma.domain.text.data.db.Text;

import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name = "apikey")
public class ApiKey implements MutableEntity<ApiKeyDTO> {

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

    public ApiKey(ApiKeyDTO apiKey) {
        assert(apiKey.getId() != null);
        assert(apiKey.getKey() != null);

        this.id = apiKey.getId();
        this.key = apiKey.getKey();

        if(this.description == null) {
            this.description = new Text();
        }

        apply(apiKey);
    }

    @Override
    public void apply(ApiKeyDTO ak){
        assert(this.id == ak.getId());

        this.name = ak.getName();
        this.description.apply(ak.getDescription());
    }

    @Override
    public ApiKeyDTO toDTO() {
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
