package it.chalmers.gamma.app.apikey.domain.settings;

import it.chalmers.gamma.app.apikey.domain.ApiKeyId;

public interface ApiKeySettingsRepository {
  void createEmptyAccountScaffoldSettings(ApiKeyId apiKeyId);

  void createEmptyInfoSettings(ApiKeyId apiKeyId);

  ApiKeyAccountScaffoldSettings getAccountScaffoldSettings(ApiKeyId apiKeyId);

  ApiKeyInfoSettings getInfoSettings(ApiKeyId apiKeyId);

  void setAccountScaffoldSettings(ApiKeyId apiKeyId, ApiKeyAccountScaffoldSettings settings);

  void setInfoSettings(ApiKeyId apiKeyId, ApiKeyInfoSettings settings);
}
