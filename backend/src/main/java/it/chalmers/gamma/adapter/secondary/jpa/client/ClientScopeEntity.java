package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.client.Scope;

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
