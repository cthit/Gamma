package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import jakarta.persistence.*;
import java.util.UUID;

@Embeddable
public class ApiKeySettingsSuperGroupTypePK
    extends PKId<ApiKeySettingsSuperGroupTypePK.ApiKeySettingsSuperGroupTypePKDTO> {

  @ManyToOne
  @JoinColumn(name = "settings_id")
  private ApiKeySettingsEntity settings;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "super_group_type_name")
  private SuperGroupTypeEntity superGroupType;

  protected ApiKeySettingsSuperGroupTypePK() {}

  protected ApiKeySettingsSuperGroupTypePK(
      ApiKeySettingsEntity settingsEntity, SuperGroupTypeEntity superGroupTypeEntity) {
    this.settings = settingsEntity;
    this.superGroupType = superGroupTypeEntity;
  }

  @Override
  public ApiKeySettingsSuperGroupTypePKDTO getValue() {
    return new ApiKeySettingsSuperGroupTypePKDTO(
        settings.getId(), new SuperGroupType(superGroupType.getId()));
  }

  public record ApiKeySettingsSuperGroupTypePKDTO(
      UUID apiKeySettingsId, SuperGroupType superGroupType) {}
}
