package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "apikey")
public class ApiKeyEntity extends MutableEntity<UUID> {

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

    public ApiKeyEntity() {
        description = new TextEntity();
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    public void setApiKeyToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public ApiKeyType getKeyType() {
        return keyType;
    }

    public TextEntity getDescription() {
        return description;
    }
}
