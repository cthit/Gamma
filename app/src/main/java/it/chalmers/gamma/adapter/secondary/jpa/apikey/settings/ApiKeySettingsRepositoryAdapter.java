package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeJpaRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.apikey.domain.settings.ApiKeyAccountScaffoldSettings;
import it.chalmers.gamma.app.apikey.domain.settings.ApiKeyInfoSettings;
import it.chalmers.gamma.app.apikey.domain.settings.ApiKeySettingsRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ApiKeySettingsRepositoryAdapter implements ApiKeySettingsRepository {

  private final ApiKeyJpaRepository apiKeyJpaRepository;
  private final ApiKeySettingsJpaRepository apiKeySettingsJpaRepository;
  private final SuperGroupTypeJpaRepository superGroupTypeJpaRepository;
  private final AccountScaffoldRequiresManagedJpaRepository
      accountScaffoldRequiresManagedJpaRepository;

  public ApiKeySettingsRepositoryAdapter(
      ApiKeyJpaRepository apiKeyJpaRepository,
      ApiKeySettingsJpaRepository apiKeySettingsJpaRepository,
      SuperGroupTypeJpaRepository superGroupTypeJpaRepository,
      AccountScaffoldRequiresManagedJpaRepository accountScaffoldRequiresManagedJpaRepository) {
    this.apiKeyJpaRepository = apiKeyJpaRepository;
    this.apiKeySettingsJpaRepository = apiKeySettingsJpaRepository;
    this.superGroupTypeJpaRepository = superGroupTypeJpaRepository;
    this.accountScaffoldRequiresManagedJpaRepository = accountScaffoldRequiresManagedJpaRepository;
  }

  @Override
  public void createEmptyAccountScaffoldSettings(ApiKeyId apiKeyId) {
    ApiKeySettingsEntity entity = new ApiKeySettingsEntity();

    ApiKeyEntity apiKeyEntity = this.apiKeyJpaRepository.getReferenceById(apiKeyId.value());

    if (apiKeyEntity.getKeyType() != ApiKeyType.ACCOUNT_SCAFFOLD) {
      throw new RuntimeException(
          "Can only create account scaffold settings for account scaffold api key");
    }

    entity.id = UUID.randomUUID();
    entity.apiKeyEntity = apiKeyEntity;
    entity.superGroupTypes = new ArrayList<>();

    this.apiKeySettingsJpaRepository.save(entity);
  }

  @Override
  public void createEmptyInfoSettings(ApiKeyId apiKeyId) {
    ApiKeySettingsEntity entity = new ApiKeySettingsEntity();

    ApiKeyEntity apiKeyEntity = this.apiKeyJpaRepository.getReferenceById(apiKeyId.value());

    if (apiKeyEntity.getKeyType() != ApiKeyType.INFO) {
      throw new RuntimeException("Can only create info settings for info api key");
    }

    entity.id = UUID.randomUUID();
    entity.apiKeyEntity = apiKeyEntity;
    entity.superGroupTypes = new ArrayList<>();

    this.apiKeySettingsJpaRepository.save(entity);
  }

  @Override
  public ApiKeyAccountScaffoldSettings getAccountScaffoldSettings(ApiKeyId apiKeyId) {
    ApiKeySettingsEntity apiKeySettingsEntity =
        this.apiKeySettingsJpaRepository.getApiKeySettingsEntityByApiKeyEntity_Id(apiKeyId.value());

    if (apiKeySettingsEntity.apiKeyEntity == null) {
      throw new RuntimeException("Unexpected null");
    }

    if (apiKeySettingsEntity.apiKeyEntity.getKeyType() != ApiKeyType.ACCOUNT_SCAFFOLD) {
      throw new RuntimeException("Unexpected api key type");
    }

    return new ApiKeyAccountScaffoldSettings(
        apiKeySettingsEntity.getVersion(),
        apiKeySettingsEntity.superGroupTypes.stream()
            .map(
                entity ->
                    new ApiKeyAccountScaffoldSettings.Row(
                        entity.getSuperGroupType(),
                        this.accountScaffoldRequiresManagedJpaRepository.existsById(
                            new AccountScaffoldRequiresManagedPK(
                                apiKeySettingsEntity.id, entity.getSuperGroupType().value()))))
            .toList());
  }

  @Override
  public ApiKeyInfoSettings getInfoSettings(ApiKeyId apiKeyId) {
    ApiKeySettingsEntity apiKeySettingsEntity =
        this.apiKeySettingsJpaRepository.getApiKeySettingsEntityByApiKeyEntity_Id(apiKeyId.value());

    if (apiKeySettingsEntity.apiKeyEntity == null) {
      throw new RuntimeException("Unexpected null");
    }

    if (apiKeySettingsEntity.apiKeyEntity.getKeyType() != ApiKeyType.INFO) {
      throw new RuntimeException("Unexpected api key type");
    }

    return new ApiKeyInfoSettings(
        apiKeySettingsEntity.getVersion(),
        apiKeySettingsEntity.superGroupTypes.stream()
            .map(ApiKeySettingsSuperGroupTypeEntity::getSuperGroupType)
            .toList());
  }

  @Transactional
  @Override
  public void setAccountScaffoldSettings(
      ApiKeyId apiKeyId, ApiKeyAccountScaffoldSettings settings) {
    ApiKeySettingsEntity apiKeySettingsEntity =
        this.apiKeySettingsJpaRepository.getApiKeySettingsEntityByApiKeyEntity_Id(apiKeyId.value());

    if (apiKeySettingsEntity.apiKeyEntity.getKeyType() != ApiKeyType.ACCOUNT_SCAFFOLD) {
      throw new RuntimeException("Unexpected api key type");
    }

    apiKeySettingsEntity.superGroupTypes.clear();
    apiKeySettingsEntity.superGroupTypes.addAll(
        settings.superGroupTypes().stream()
            .map(
                superGroupType ->
                    new ApiKeySettingsSuperGroupTypeEntity(
                        apiKeySettingsEntity,
                        superGroupTypeJpaRepository.getReferenceById(
                            superGroupType.type().value())))
            .toList());

    for (var row : settings.superGroupTypes()) {
      var pk = new AccountScaffoldRequiresManagedPK(apiKeySettingsEntity.id, row.type().value());
      var superGroupTypeRequiresManagedOptional = accountScaffoldRequiresManagedJpaRepository.findById(pk);
      if (row.requiresManaged() && superGroupTypeRequiresManagedOptional.isEmpty()) {
        accountScaffoldRequiresManagedJpaRepository.save(
            new AccountScaffoldRequiresManagedEntity(apiKeySettingsEntity.id, row.type().value()));
      } else if (!row.requiresManaged() && superGroupTypeRequiresManagedOptional.isPresent()) {
        accountScaffoldRequiresManagedJpaRepository.deleteById(pk);
      }
    }

    apiKeySettingsEntity.increaseVersion(settings.version());

    this.apiKeySettingsJpaRepository.save(apiKeySettingsEntity);
  }

  @Transactional
  @Override
  public void setInfoSettings(ApiKeyId apiKeyId, ApiKeyInfoSettings settings) {
    ApiKeySettingsEntity apiKeySettingsEntity =
        this.apiKeySettingsJpaRepository.getApiKeySettingsEntityByApiKeyEntity_Id(apiKeyId.value());

    if (apiKeySettingsEntity.apiKeyEntity.getKeyType() != ApiKeyType.INFO) {
      throw new RuntimeException("Unexpected api key type");
    }

    apiKeySettingsEntity.superGroupTypes.clear();
    apiKeySettingsEntity.superGroupTypes.addAll(
        settings.superGroupTypes().stream()
            .map(
                superGroupType ->
                    new ApiKeySettingsSuperGroupTypeEntity(
                        apiKeySettingsEntity,
                        superGroupTypeJpaRepository.getReferenceById(superGroupType.value())))
            .toList());

    apiKeySettingsEntity.increaseVersion(settings.version());

    this.apiKeySettingsJpaRepository.save(apiKeySettingsEntity);
  }
}
