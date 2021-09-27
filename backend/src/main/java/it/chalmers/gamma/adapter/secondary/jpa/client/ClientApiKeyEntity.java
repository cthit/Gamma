package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntity;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "itclient_apikey")
public class ClientApiKeyEntity extends ImmutableEntity<ClientId> {

    @Id
    @Column(name = "client_id")
    private String clientId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "client_id")
    private ClientEntity client;

    @OneToOne
    @JoinColumn(name = "api_key_id")
    private ApiKeyEntity apiKey;

    protected ClientApiKeyEntity() {

    }

    protected ClientApiKeyEntity(ClientEntity clientEntity, ApiKeyEntity apiKeyEntity) {
        this.client = clientEntity;
        this.apiKey = apiKeyEntity;
    }

    @Override
    protected ClientId id() {
        return this.client.id();
    }

}
