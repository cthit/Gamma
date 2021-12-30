package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.client.domain.ClientUid;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "itclient_apikey")
public class ClientApiKeyEntity extends ImmutableEntity<UUID> {

    @Id
    @Column(name = "client_uid")
    private UUID clientUid;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "client_uid")
    private ClientEntity client;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "api_key_id")
    private ApiKeyEntity apiKey;

    protected ClientApiKeyEntity() {

    }

    protected ClientApiKeyEntity(ClientEntity clientEntity, ApiKeyEntity apiKeyEntity) {
        this.client = clientEntity;
        this.clientUid = clientEntity.clientUid;
        this.apiKey = apiKeyEntity;
    }

    @Override
    public UUID getId() {
        return this.clientUid;
    }

    public ClientEntity getClient() {
        return this.client;
    }

    public ApiKeyEntity getApiKeyEntity() {
        return this.apiKey;
    }

}
