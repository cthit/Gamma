package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.apikey.domain.*;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyBootstrap.class);

  private final ApiKeyRepository apiKeyRepository;
  private final BootstrapSettings bootstrapSettings;

  public ApiKeyBootstrap(ApiKeyRepository apiKeyRepository, BootstrapSettings bootstrapSettings) {
    this.apiKeyRepository = apiKeyRepository;
    this.bootstrapSettings = bootstrapSettings;
  }

  public void ensureApiKeys() {
    if (!this.bootstrapSettings.mocking() || this.apiKeyRepository.getAll().size() != 1) {
      return;
    }

    LOGGER.info("========== API BOOTSTRAP ==========");
    LOGGER.info("Generating mock api keys...");

    for (ApiKeyType apiKeyType : ApiKeyType.values()) {
      if (apiKeyType != ApiKeyType.CLIENT) {
        ApiKeyToken apiKeyToken = new ApiKeyToken(apiKeyType.name() + "-super-secret-code");
        this.apiKeyRepository.create(
            new ApiKey(
                ApiKeyId.generate(),
                new PrettyName(apiKeyType.name() + "-mock"),
                new Text(),
                apiKeyType,
                apiKeyToken));

        LOGGER.info(
            "Api key of type "
                + apiKeyType.name()
                + " has been generated with code: "
                + apiKeyToken.value());
      }
    }
    LOGGER.info("==========               ==========");
  }
}
