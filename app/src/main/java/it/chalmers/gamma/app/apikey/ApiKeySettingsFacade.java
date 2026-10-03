package it.chalmers.gamma.app.apikey;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSpecificApi;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyScopeSettings.SuperGroupTypeConfig;
import it.chalmers.gamma.app.apikey.domain.ApiKeySuperGroupTypeRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ApiKeySettingsFacade extends Facade {

  private final ApiKeySuperGroupTypeRepository apiKeySuperGroupTypeRepository;

  public ApiKeySettingsFacade(
      AccessGuard accessGuard, ApiKeySuperGroupTypeRepository apiKeySuperGroupTypeRepository) {
    super(accessGuard);
    this.apiKeySuperGroupTypeRepository = apiKeySuperGroupTypeRepository;
  }

  public record SuperGroupTypeConfigDTO(String type, boolean gdprFilter) {}

  public List<SuperGroupTypeConfigDTO> getUnifiedSettings(UUID apiKeyId) {
    accessGuard.requireEither(isAdmin(), isSpecificApi(new ApiKeyId(apiKeyId)));

    return this.apiKeySuperGroupTypeRepository.get(apiKeyId).stream()
        .map(c -> new SuperGroupTypeConfigDTO(c.type().value(), c.gdprFilter()))
        .toList();
  }

  public void setUnifiedSettings(UUID apiKeyId, List<SuperGroupTypeConfigDTO> configs) {
    accessGuard.require(isAdmin());

    this.apiKeySuperGroupTypeRepository.set(
        apiKeyId,
        configs.stream()
            .map(c -> new SuperGroupTypeConfig(new SuperGroupType(c.type), c.gdprFilter()))
            .toList());
  }
}
