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
    private UUID id;

    @Column(name = "token")
    private String token;

    @Column(name = "pretty_name")
    private String prettyName;

    @Enumerated(EnumType.STRING)
    private ApiKeyType keyType;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.ALL)
    private TextEntity description;

    ApiKeyEntity() {
        description = new TextEntity();
    }

    public ApiKeyEntity(UUID id,
                        String token,
                        String prettyName,
                        ApiKeyType keyType,
                        TextEntity description) {
        this.id = id;
        this.token = token;
        this.prettyName = prettyName;
        this.keyType = keyType;
        this.description = description;
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
