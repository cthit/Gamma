package it.chalmers.gamma.adapter.secondary.jpa.client.scope;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.Scope;
import jakarta.persistence.*;

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

  @Override
  public ClientScopePKDTO getValue() {
    return new ClientScopePKDTO(new ClientUid(this.client.getId()), this.scope);
  }

  public record ClientScopePKDTO(ClientUid clientUid, Scope scope) {}
}
