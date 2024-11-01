package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "g_client_restriction")
public class ClientRestrictionEntity extends ImmutableEntity<UUID> {

  @Column(name = "restriction_id", columnDefinition = "uuid")
  protected UUID restrictionId;

  @Id
  @Column(name = "client_uid", columnDefinition = "uuid")
  protected UUID clientUid;

  @OneToOne
  @PrimaryKeyJoinColumn(name = "client_uid")
  protected ClientEntity client;

  @OneToMany(mappedBy = "id.clientRestriction", cascade = CascadeType.ALL, orphanRemoval = true)
  protected List<ClientRestrictionSuperGroupEntity> superGroupRestrictions;

  public ClientRestrictionEntity() {}

  public ClientRestrictionEntity(UUID restrictionId, UUID clientUid) {
    this.restrictionId = restrictionId;
    this.clientUid = clientUid;
    this.superGroupRestrictions = new ArrayList<>();
  }

  public void setSuperGroupRestrictions(
      List<ClientRestrictionSuperGroupEntity> superGroupRestrictions) {
    this.superGroupRestrictions = superGroupRestrictions;
  }

  public List<ClientRestrictionSuperGroupEntity> getSuperGroupRestrictions() {
    return superGroupRestrictions;
  }

  @Override
  public UUID getId() {
    return restrictionId;
  }
}
