package it.chalmers.gamma.app.apikey.service;

import it.chalmers.gamma.app.domain.ApiKey;
import it.chalmers.gamma.app.domain.ApiKeyId;
import it.chalmers.gamma.app.domain.ApiKeyToken;
import it.chalmers.gamma.app.domain.ApiKeyType;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.app.service.TextEntity;
import it.chalmers.gamma.util.entity.ImmutableEntity;

import javax.persistence.*;

@Entity
@Table(name = "apikey")
public class ApiKeyEntity extends ImmutableEntity<ApiKeyId, ApiKey> {

    @EmbeddedId
    private ApiKeyId id;

    @Embedded
    private ApiKeyToken token;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.MERGE)
    private TextEntity description;

    @Embedded
    private PrettyName prettyName;

    @Enumerated(EnumType.STRING)
    private ApiKeyType keyType;

    protected ApiKeyEntity() { }

    protected ApiKeyEntity(ApiKey apiKey, ApiKeyToken apiKeyToken) {
        assert(apiKey.id() != null);
        assert(apiKeyToken != null);

        this.id = apiKey.id();
        this.token = apiKeyToken;
        this.prettyName = apiKey.prettyName();
        this.description = new TextEntity(apiKey.description());
        this.keyType = apiKey.keyType();
    }

    protected void setToken(ApiKeyToken token) {
        this.token = token;
    }

    @Override
    protected ApiKey toDTO() {
        return new ApiKey(
                this.id,
                this.prettyName,
                this.description.toDTO(),
                this.keyType
        );
    }

    @Override
    protected ApiKeyId id() {
        return this.id;
    }

}
