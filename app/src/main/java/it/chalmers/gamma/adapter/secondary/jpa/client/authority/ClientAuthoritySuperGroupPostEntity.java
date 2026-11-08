package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "g_client_authority_super_group_post")
public class ClientAuthoritySuperGroupPostEntity
    extends ImmutableEntity<ClientAuthoritySuperGroupPostPK> {

  @EmbeddedId protected ClientAuthoritySuperGroupPostPK id;

  protected ClientAuthoritySuperGroupPostEntity() {}

  public ClientAuthoritySuperGroupPostEntity(
      SuperGroupEntity superGroup, PostEntity post, ClientAuthorityEntity clientAuthorityEntity) {
    this.id = new ClientAuthoritySuperGroupPostPK(superGroup, post, clientAuthorityEntity);
  }

  @Override
  public ClientAuthoritySuperGroupPostPK getId() {
    return this.id;
  }

  protected SuperGroupEntity getSuperGroup() {
    return this.id.superGroupEntity;
  }

  protected PostEntity getPost() {
    return this.id.postEntity;
  }
}
