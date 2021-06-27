package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.app.domain.ApiKey;
import it.chalmers.gamma.app.domain.ApiKeyId;
import it.chalmers.gamma.app.domain.ApiKeyToken;
import it.chalmers.gamma.app.domain.ApiKeyType;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.app.service.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

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

    protected ApiKeyEntity(ApiKey apiKey) {
        assert(apiKey.id() != null);
        assert(apiKey.apiKeyToken() != null);

        this.id = apiKey.id();
        this.token = apiKey.apiKeyToken();
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
                this.keyType,
                this.token
        );
    }

    @Override
    protected ApiKeyId id() {
        return this.id;
    }

}
