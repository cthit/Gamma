package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.apikey.ApiKeyFacade;
import it.chalmers.gamma.app.apikey.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyBootstrap.class);

  private final ApiKeyFacade apiKeyFacade;
  private final BootstrapSettings bootstrapSettings;

  public ApiKeyBootstrap(BootstrapSettings bootstrapSettings, ApiKeyFacade apiKeyFacade) {
    this.apiKeyFacade = apiKeyFacade;
    this.bootstrapSettings = bootstrapSettings;
  }

  public void ensureApiKeys() {
    if (!this.bootstrapSettings.mocking() || !this.apiKeyFacade.getAll().isEmpty()) {
      return;
    }

    LOGGER.info("========== API BOOTSTRAP ==========");
    LOGGER.info("Generating mock api keys...");

    for (ApiKeyType apiKeyType : ApiKeyType.values()) {
      if (apiKeyType != ApiKeyType.CLIENT) {

        var createdApiKey =
            this.apiKeyFacade.create(
                new ApiKeyFacade.NewApiKey(
                    apiKeyType.name().toLowerCase() + "-mock", "", "", apiKeyType.name()));

        LOGGER.info(
            "Api key of type "
                + apiKeyType.name()
                + " has been generated with id: "
                + createdApiKey.apiKey().id()
                + " and code: "
                + createdApiKey.token());
      }
    }

    LOGGER.info("Add the header: Authorization: pre-shared <ID>:<TOKEN> to start using the APIs");
    LOGGER.info("==========               ==========");
  }
}
