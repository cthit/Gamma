package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.app.domain.apikey.ApiKey;
import it.chalmers.gamma.app.domain.apikey.ApiKeyId;
import it.chalmers.gamma.app.domain.apikey.ApiKeyToken;
import it.chalmers.gamma.app.domain.apikey.ApiKeyType;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "apikey")
public class ApiKeyEntity extends ImmutableEntity<ApiKeyId> {

    @Id
    @Column(name = "api_key_id")
    private UUID id;

    @Column(name = "token")
    private String token;

    @Column(name = "pretty_name")
    private String prettyName;

    @Enumerated(EnumType.STRING)
    private ApiKeyType keyType;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.MERGE)
    private TextEntity description;

    protected ApiKeyEntity() { }

    public ApiKeyEntity(ApiKey apiKey) {
        assert(apiKey.id() != null);
        assert(apiKey.apiKeyToken() != null);

        this.id = apiKey.id().getValue();
        this.token = apiKey.apiKeyToken().value();
        this.prettyName = apiKey.prettyName().value();
        this.description = new TextEntity(apiKey.description());
        this.keyType = apiKey.keyType();
    }

    public ApiKey toDomain() {
        return new ApiKey(
                new ApiKeyId(this.id),
                new PrettyName(this.prettyName),
                this.description.toDomain(),
                this.keyType,
                new ApiKeyToken(this.token)
        );
    }

    @Override
    protected ApiKeyId id() {
        return new ApiKeyId(this.id);
    }

}
