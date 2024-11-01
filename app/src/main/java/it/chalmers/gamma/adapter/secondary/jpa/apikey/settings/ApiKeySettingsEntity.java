package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "g_api_key_settings")
public class ApiKeySettingsEntity extends MutableEntity<UUID> {

  @Id
  @Column(name = "settings_id", columnDefinition = "uuid")
  protected UUID id;

  @JoinColumn(name = "api_key_id")
  @OneToOne(cascade = CascadeType.ALL)
  protected ApiKeyEntity apiKeyEntity;

  @OneToMany(
      mappedBy = "id.settings",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  protected List<ApiKeySettingsSuperGroupTypeEntity> superGroupTypes;

  @Override
  public UUID getId() {
    return this.id;
  }
}
