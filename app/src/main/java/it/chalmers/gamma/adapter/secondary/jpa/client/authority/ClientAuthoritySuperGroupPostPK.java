package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class ClientAuthoritySuperGroupPostPK
    extends PKId<ClientAuthoritySuperGroupPostPK.AuthoritySuperGroupPostPKRecord> {

  @JoinColumn(name = "super_group_id")
  @ManyToOne
  protected SuperGroupEntity superGroupEntity;

  @JoinColumn(name = "post_id")
  @ManyToOne
  protected PostEntity postEntity;

  @Embedded protected ClientAuthorityEntityPK clientAuthority;

  protected ClientAuthoritySuperGroupPostPK() {}

  protected ClientAuthoritySuperGroupPostPK(
      SuperGroupEntity superGroupEntity,
      PostEntity postEntity,
      ClientAuthorityEntity clientAuthority) {
    this.superGroupEntity = superGroupEntity;
    this.postEntity = postEntity;
    this.clientAuthority = clientAuthority.getId();
  }

  @Override
  public AuthoritySuperGroupPostPKRecord getValue() {
    return new AuthoritySuperGroupPostPKRecord(
        new SuperGroupId(this.superGroupEntity.getId()),
        new PostId(this.postEntity.getId()),
        new AuthorityName(this.clientAuthority.name),
        new ClientUid(this.clientAuthority.client.getId()));
  }

  public record AuthoritySuperGroupPostPKRecord(
      SuperGroupId superGroupId, PostId postId, AuthorityName authorityName, ClientUid clientUid) {}
}
