package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.internal.text.data.db.Text;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.util.domain.abstraction.Id;
import org.springframework.data.annotation.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "apikey")
public class ApiKey extends ImmutableEntity {

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
        this.description = new Text(apiKey.description());
        this.keyType = apiKey.keyType();
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
    protected Id id() {
        return this.id;
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
