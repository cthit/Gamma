package it.chalmers.gamma.adapter.secondary.jpa.apikey.settings;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeySettingsJpaRepository extends JpaRepository<ApiKeySettingsEntity, UUID> {

  ApiKeySettingsEntity getApiKeySettingsEntityByApiKeyEntity_Id(UUID apiKeyId);
}
