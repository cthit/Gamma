package it.chalmers.gamma.internal.client.apikey.service;

import it.chalmers.gamma.domain.ApiKeyId;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "itclient_apikey")
public class ClientApiKeyEntity extends ImmutableEntity<ClientId, ClientApiKeyDTO> {

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

    @Override
    protected ClientApiKeyDTO toDTO() {
        return new ClientApiKeyDTO(
                this.clientId,
                this.apiKeyId
        );
    }
}
