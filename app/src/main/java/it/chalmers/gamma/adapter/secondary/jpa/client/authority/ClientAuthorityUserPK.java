package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class ClientAuthorityUserPK extends PKId<ClientAuthorityUserPK.AuthorityUserPKRecord> {

  @JoinColumn(name = "user_id")
  @ManyToOne
  private UserEntity userEntity;

  @Embedded protected ClientAuthorityEntityPK clientAuthority;

  protected ClientAuthorityUserPK() {}

  public ClientAuthorityUserPK(UserEntity userEntity, ClientAuthorityEntity clientAuthority) {
    this.userEntity = userEntity;
    this.clientAuthority = clientAuthority.getId();
  }

  @Override
  public AuthorityUserPKRecord getValue() {
    return new AuthorityUserPKRecord(
        new UserId(this.userEntity.getId()),
        new AuthorityName(this.clientAuthority.name),
        new ClientUid(this.clientAuthority.client.getId()));
  }

  public UserEntity getUserEntity() {
    return this.userEntity;
  }

  protected record AuthorityUserPKRecord(
      UserId userId, AuthorityName authorityName, ClientUid clientUid) {}
}
