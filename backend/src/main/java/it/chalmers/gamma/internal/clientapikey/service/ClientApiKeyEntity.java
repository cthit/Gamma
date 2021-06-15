package it.chalmers.gamma.internal.clientapikey.service;

import it.chalmers.gamma.domain.ApiKeyId;
import it.chalmers.gamma.domain.ClientApiKeyPair;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "itclient_apikey")
public class ClientApiKeyEntity extends ImmutableEntity<ClientId, ClientApiKeyPair> {

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
    protected ClientApiKeyPair toDTO() {
        return new ClientApiKeyPair(
                this.clientId,
                this.apiKeyId
        );
    }
}
