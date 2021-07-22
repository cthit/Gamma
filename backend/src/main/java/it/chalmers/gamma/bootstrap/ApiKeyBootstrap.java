package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.apikey.ApiKeyRepository;
import it.chalmers.gamma.app.domain.ApiKey;
import it.chalmers.gamma.app.domain.ApiKeyId;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.app.domain.ApiKeyToken;
import it.chalmers.gamma.app.domain.ApiKeyType;
import it.chalmers.gamma.app.domain.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ApiKeyBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyBootstrap.class);

    private final ApiKeyRepository apiKeyRepository;
    private final boolean mocking;

    public ApiKeyBootstrap(ApiKeyRepository apiKeyRepository,
                           @Value("${application.mocking}") boolean mocking) {
        this.apiKeyRepository = apiKeyRepository;
        this.mocking = mocking;
    }

    @PostConstruct
    public void ensureApiKeys() {
        if (!mocking || !this.apiKeyRepository.getAll().isEmpty()) {
            return;
        }

        LOGGER.info("========== API BOOTSTRAP ==========");
        LOGGER.info("Generating mock api keys...");

        for (ApiKeyType apiKeyType : ApiKeyType.values()) {
            if (apiKeyType != ApiKeyType.CLIENT) {
                ApiKeyToken apiKeyToken = new ApiKeyToken(apiKeyType.name() + "-super-secret-token");
                this.apiKeyRepository.create(
                        new ApiKey(
                                ApiKeyId.generate(),
                                new PrettyName(apiKeyType.name() + "-mock"),
                                new Text(),
                                apiKeyType,
                                apiKeyToken
                        )
                );

                LOGGER.info("Api key of type " + apiKeyType.name() + " has been generated with token: " + apiKeyToken.value());
            }
        }
        LOGGER.info("========== API BOOTSTRAP ==========");
    }

}
