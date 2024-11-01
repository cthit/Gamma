package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.client.domain.restriction.ClientRestrictionId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class ClientRestrictionSuperGroupPK
    extends PKId<ClientRestrictionSuperGroupPK.ClientRestrictionPKDTO> {

  @ManyToOne
  @JoinColumn(name = "restriction_id")
  private ClientRestrictionEntity clientRestriction;

  @JoinColumn(name = "super_group_id")
  @ManyToOne
  protected SuperGroupEntity superGroupEntity;

  protected ClientRestrictionSuperGroupPK() {}

  protected ClientRestrictionSuperGroupPK(
      ClientRestrictionEntity clientRestrictionEntity, SuperGroupEntity superGroupEntity) {
    this.clientRestriction = clientRestrictionEntity;
    this.superGroupEntity = superGroupEntity;
  }

  @Override
  public ClientRestrictionPKDTO getValue() {
    return new ClientRestrictionPKDTO(
        new ClientRestrictionId(this.clientRestriction.restrictionId),
        new SuperGroupId(this.superGroupEntity.getId()));
  }

  public record ClientRestrictionPKDTO(
      ClientRestrictionId clientRestrictionId, SuperGroupId superGroupId) {}
}
