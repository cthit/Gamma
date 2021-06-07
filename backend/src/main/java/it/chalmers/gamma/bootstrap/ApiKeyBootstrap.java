package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.domain.ApiKey;
import it.chalmers.gamma.domain.ApiKeyId;
import it.chalmers.gamma.internal.apikey.service.ApiKeyService;
import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.domain.ApiKeyType;
import it.chalmers.gamma.domain.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ApiKeyBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyBootstrap.class);

    private final ApiKeyService apiKeyService;
    private final boolean mocking;

    public ApiKeyBootstrap(ApiKeyService apiKeyService,
                           @Value("${application.mocking}") boolean mocking) {
        this.apiKeyService = apiKeyService;
        this.mocking = mocking;
    }

    @PostConstruct
    public void ensureApiKeys() {
        if (!mocking || !this.apiKeyService.getAll().isEmpty()) {
            return;
        }

        LOGGER.info("========== API BOOTSTRAP ==========");
        LOGGER.info("Generating mock api keys...");

        for (ApiKeyType apiKeyType : ApiKeyType.values()) {
            if (apiKeyType != ApiKeyType.CLIENT) {
                ApiKeyToken apiKeyToken = ApiKeyToken.valueOf(apiKeyType.name() + "-super-secret-token");
                this.apiKeyService.create(
                        new ApiKey(
                                new ApiKeyId(),
                                EntityName.valueOf(apiKeyType.name() + "-mock"),
                                new Text("", ""),
                                apiKeyType
                        ),
                        apiKeyToken
                );

                LOGGER.info("Api key of type " + apiKeyType.name() + " has been generated with token: " + apiKeyToken.get());
            }
        }
        LOGGER.info("========== API BOOTSTRAP ==========");
    }

}
