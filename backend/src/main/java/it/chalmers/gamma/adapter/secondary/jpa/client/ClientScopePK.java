package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntity;
import it.chalmers.gamma.app.domain.PKId;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.app.domain.client.ClientUid;
import it.chalmers.gamma.app.domain.client.Scope;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class ClientScopePK extends PKId<ClientScopePK.ClientScopePKDTO> {

    @ManyToOne
    @JoinColumn(name = "client_uid")
    private ClientEntity client;

    @Column(name = "scope")
    @Enumerated(EnumType.STRING)
    private Scope scope;

    protected ClientScopePK() {}

    protected ClientScopePK(ClientEntity clientEntity, Scope scope) {
        this.client = clientEntity;
        this.scope = scope;
    }

    protected record ClientScopePKDTO(ClientUid clientUid,
                                      Scope scope) { }

    @Override
    public ClientScopePKDTO getValue() {
        return new ClientScopePKDTO(
                this.client.domainId(),
                this.scope
        );
    }

}
