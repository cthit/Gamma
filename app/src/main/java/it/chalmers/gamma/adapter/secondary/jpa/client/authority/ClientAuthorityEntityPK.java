package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class ClientAuthorityEntityPK extends PKId<ClientAuthorityEntityPK.AuthorityEntityPKRecord> {

  @Column(name = "authority_name")
  protected String name;

  @JoinColumn(name = "client_uid")
  @ManyToOne
  protected ClientEntity client;

  protected ClientAuthorityEntityPK() {}

  protected ClientAuthorityEntityPK(ClientEntity client, String name) {
    this.client = client;
    this.name = name;
  }

  @Override
  public AuthorityEntityPKRecord getValue() {
    return new AuthorityEntityPKRecord(
        new ClientUid(this.client.getId()), new AuthorityName(this.name));
  }

  public record AuthorityEntityPKRecord(ClientUid clientUid, AuthorityName authorityName) {}
}
