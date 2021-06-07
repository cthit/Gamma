package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.domain.ApiKey;
import it.chalmers.gamma.domain.ApiKeyId;
import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.domain.ApiKeyType;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.internal.text.service.TextEntity;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

import javax.persistence.*;

@Entity
@Table(name = "apikey")
public class ApiKeyEntity extends ImmutableEntity<ApiKeyId, ApiKey> {

    @EmbeddedId
    private ApiKeyId id;

    @Embedded
    private ApiKeyToken key;

    @JoinColumn(name = "description")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private TextEntity description;

    @Embedded
    private EntityName name;

    @Enumerated(EnumType.STRING)
    private ApiKeyType keyType;

    protected ApiKeyEntity() { }

    protected ApiKeyEntity(ApiKey apiKey, ApiKeyToken apiKeyToken) {
        assert(apiKey.id() != null);
        assert(apiKeyToken != null);

        this.id = apiKey.id();
        this.key = apiKeyToken;
        this.name = apiKey.name();
        this.description = new TextEntity(apiKey.description());
        this.keyType = apiKey.keyType();
    }

    @Override
    protected ApiKey toDTO() {
        return new ApiKey(
                this.id,
                this.name,
                this.description.toDTO(),
                this.keyType
        );
    }

    @Override
    protected ApiKeyId id() {
        return this.id;
    }

}
