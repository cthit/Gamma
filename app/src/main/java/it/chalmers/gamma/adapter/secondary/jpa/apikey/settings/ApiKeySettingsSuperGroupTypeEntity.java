package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "g_api_key_to_super_group_type")
public class ApiKeySettingsSuperGroupTypeEntity
    extends ImmutableEntity<ApiKeySettingsSuperGroupTypePK> {

  @EmbeddedId private ApiKeySettingsSuperGroupTypePK id;

  protected ApiKeySettingsSuperGroupTypeEntity() {}

  protected ApiKeySettingsSuperGroupTypeEntity(
      ApiKeySettingsEntity apiKeySettingsEntity, SuperGroupTypeEntity superGroupTypeEntity) {
    this.id = new ApiKeySettingsSuperGroupTypePK(apiKeySettingsEntity, superGroupTypeEntity);
  }

  @Override
  public ApiKeySettingsSuperGroupTypePK getId() {
    return this.id;
  }

  public SuperGroupType getSuperGroupType() {
    return this.id.getValue().superGroupType();
  }
}
