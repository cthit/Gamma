package it.chalmers.gamma.app.apikey;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSpecificApi;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.settings.ApiKeyAccountScaffoldSettings;
import it.chalmers.gamma.app.apikey.domain.settings.ApiKeyInfoSettings;
import it.chalmers.gamma.app.apikey.domain.settings.ApiKeySettingsRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ApiKeySettingsFacade extends Facade {

  private final ApiKeySettingsRepository apiKeySettingsRepository;

  public ApiKeySettingsFacade(
      AccessGuard accessGuard, ApiKeySettingsRepository apiKeySettingsRepository) {
    super(accessGuard);
    this.apiKeySettingsRepository = apiKeySettingsRepository;
  }

  public record ApiKeySettingsInfoDTO(int version, List<String> superGroupTypes) {}

  public ApiKeySettingsInfoDTO getInfoSettings(UUID apiKeyId) {
    ApiKeyId id = new ApiKeyId(apiKeyId);

    accessGuard.requireEither(isAdmin(), isSpecificApi(id));

    ApiKeyInfoSettings infoSettings = this.apiKeySettingsRepository.getInfoSettings(id);

    return new ApiKeySettingsInfoDTO(
        infoSettings.version(),
        infoSettings.superGroupTypes().stream().map(SuperGroupType::value).toList());
  }

  public void setInfoSettings(UUID apiKeyId, ApiKeySettingsInfoDTO settings) {
    accessGuard.require(isAdmin());

    this.apiKeySettingsRepository.setInfoSettings(
        new ApiKeyId(apiKeyId),
        new ApiKeyInfoSettings(
            settings.version, settings.superGroupTypes.stream().map(SuperGroupType::new).toList()));
  }

  public record AccountScaffoldTypeDTO(String type, boolean requiresManaged) {}

  public record ApiKeySettingsAccountScaffoldDTO(
      int version, List<AccountScaffoldTypeDTO> superGroupTypes) {}

  public ApiKeySettingsAccountScaffoldDTO getAccountScaffoldSettings(UUID apiKeyId) {
    ApiKeyId id = new ApiKeyId(apiKeyId);

    accessGuard.requireEither(isAdmin(), isSpecificApi(id));

    ApiKeyAccountScaffoldSettings accountScaffoldSettings =
        this.apiKeySettingsRepository.getAccountScaffoldSettings(id);

    return new ApiKeySettingsAccountScaffoldDTO(
        accountScaffoldSettings.version(),
        accountScaffoldSettings.superGroupTypes().stream()
            .map(row -> new AccountScaffoldTypeDTO(row.type().value(), row.requiresManaged()))
            .toList());
  }

  public void setAccountScaffoldSettings(UUID apiKeyId, ApiKeySettingsAccountScaffoldDTO settings) {
    accessGuard.require(isAdmin());

    this.apiKeySettingsRepository.setAccountScaffoldSettings(
        new ApiKeyId(apiKeyId),
        new ApiKeyAccountScaffoldSettings(
            settings.version,
            settings.superGroupTypes.stream()
                .map(
                    row ->
                        new ApiKeyAccountScaffoldSettings.Row(
                            new SuperGroupType(row.type), row.requiresManaged))
                .toList()));
  }
}
