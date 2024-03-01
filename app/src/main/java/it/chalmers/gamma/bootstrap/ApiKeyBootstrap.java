package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.apikey.domain.*;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyBootstrap.class);

  private final ApiKeyRepository apiKeyRepository;
  private final BootstrapSettings bootstrapSettings;
  private final PasswordEncoder passwordEncoder;

  public ApiKeyBootstrap(
      ApiKeyRepository apiKeyRepository,
      BootstrapSettings bootstrapSettings,
      PasswordEncoder passwordEncoder) {
    this.apiKeyRepository = apiKeyRepository;
    this.bootstrapSettings = bootstrapSettings;
    this.passwordEncoder = passwordEncoder;
  }

  public void ensureApiKeys() {
    if (!this.bootstrapSettings.mocking() || !this.apiKeyRepository.getAll().isEmpty()) {
      return;
    }

    LOGGER.info("========== API BOOTSTRAP ==========");
    LOGGER.info("Generating mock api keys...");

    for (ApiKeyType apiKeyType : ApiKeyType.values()) {
      if (apiKeyType != ApiKeyType.CLIENT) {
        ApiKeyToken.GeneratedApiKeyToken generated = ApiKeyToken.generate(passwordEncoder);
        ApiKeyId id = ApiKeyId.generate();
        this.apiKeyRepository.create(
            new ApiKey(
                id,
                new PrettyName(apiKeyType.name() + "-mock"),
                new Text(),
                apiKeyType,
                generated.apiKeyToken()));

        LOGGER.info(
            "Api key of type "
                + apiKeyType.name()
                + " has been generated with code: "
                + generated.rawToken()
                + " and id: "
                + id.value());
      }
    }

    LOGGER.info("Add the header: Authorization: pre-shared <ID>:<TOKEN> to start using the APIs");
    LOGGER.info("==========               ==========");
  }
}
