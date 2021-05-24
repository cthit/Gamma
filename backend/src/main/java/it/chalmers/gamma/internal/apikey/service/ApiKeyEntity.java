package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.domain.ApiKeyId;
import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.domain.ApiKeyType;
import it.chalmers.gamma.domain.Name;
import it.chalmers.gamma.internal.text.service.TextEntity;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

import javax.persistence.*;

@Entity
@Table(name = "apikey")
public class ApiKeyEntity extends ImmutableEntity<ApiKeyId, ApiKeyDTO> {

    @EmbeddedId
    private ApiKeyId id;

    @Embedded
    private ApiKeyToken key;

    @JoinColumn(name = "description")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private TextEntity description;

    @Embedded
    private Name name;

    @Enumerated(EnumType.STRING)
    private ApiKeyType keyType;

    protected ApiKeyEntity() { }

    protected ApiKeyEntity(ApiKeyDTO apiKey) {
        assert(apiKey.id() != null);
        assert(apiKey.key() != null);

        this.id = apiKey.id();
        this.key = apiKey.key();
        this.name = apiKey.name();
        this.description = new TextEntity(apiKey.description());
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
    protected ApiKeyId id() {
        return this.id;
    }

}
