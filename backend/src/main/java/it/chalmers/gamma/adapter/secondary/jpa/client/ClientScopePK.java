package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.Scope;

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
                this.client.getId(),
                this.scope
        );
    }

}
