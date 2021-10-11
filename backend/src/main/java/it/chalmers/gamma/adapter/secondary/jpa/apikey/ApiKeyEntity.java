package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
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
public class ApiKeyEntity extends MutableEntity<ApiKeyId> {

    @Id
    @Column(name = "api_key_id")
    protected UUID id;

    @Column(name = "token")
    protected String token;

    @Column(name = "pretty_name")
    protected String prettyName;

    @Enumerated(EnumType.STRING)
    protected ApiKeyType keyType;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.ALL)
    protected TextEntity description;

    protected ApiKeyEntity() { }

    @Override
    protected ApiKeyId domainId() {
        return new ApiKeyId(this.id);
    }

}
