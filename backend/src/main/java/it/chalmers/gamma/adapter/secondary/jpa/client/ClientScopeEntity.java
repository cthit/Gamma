package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.client.domain.Scope;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "itclient_scope")
public class ClientScopeEntity extends ImmutableEntity<ClientScopePK> {

    @EmbeddedId
    private ClientScopePK id;

    protected ClientScopeEntity() { }

    protected ClientScopeEntity(ClientEntity clientEntity, Scope scope) {
        this.id = new ClientScopePK(clientEntity, scope);
    }

    protected ClientScopePK domainId() {
        return this.id;
    }

    public Scope getScope() {
        return this.id.getValue().scope();
    }

}
