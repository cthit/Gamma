package it.chalmers.gamma.app.apikey.domain;

import java.util.List;
import java.util.UUID;

public interface ApiKeySuperGroupTypeRepository {

  List<ApiKeyScopeSettings.SuperGroupTypeConfig> get(UUID apiKeyId);

  void set(UUID apiKeyId, List<ApiKeyScopeSettings.SuperGroupTypeConfig> configs);
}
