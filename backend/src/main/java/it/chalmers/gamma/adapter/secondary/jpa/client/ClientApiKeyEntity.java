package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.domain.apikey.ApiKeyId;
import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "itclient_apikey")
public class ClientApiKeyEntity extends ImmutableEntity<ClientId> {

    @EmbeddedId
    private ClientId clientId;

    private ApiKeyId apiKeyId;

    protected ClientApiKeyEntity() {

    }

    protected ClientApiKeyEntity(ClientId clientId, ApiKeyId apiKeyId) {
        this.clientId = clientId;
        this.apiKeyId = apiKeyId;
    }

    @Override
    protected ClientId id() {
        return this.clientId;
    }

}
