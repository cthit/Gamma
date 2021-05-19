package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.internal.apikey.service.ApiKeyDTO;
import it.chalmers.gamma.internal.apikey.service.ApiKeyFinder;
import it.chalmers.gamma.internal.apikey.service.ApiKeyId;
import it.chalmers.gamma.internal.apikey.service.ApiKeyName;
import it.chalmers.gamma.internal.apikey.service.ApiKeyService;
import it.chalmers.gamma.internal.apikey.service.ApiKeyToken;
import it.chalmers.gamma.internal.apikey.service.ApiKeyType;
import it.chalmers.gamma.internal.text.data.dto.TextDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class ApiKeyBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyBootstrap.class);

    private final boolean mocking;
    private final ApiKeyFinder apiKeyFinder;
    private final ApiKeyService apiKeyService;

    public ApiKeyBootstrap(@Value("${application.mocking}") boolean mocking,
                           ApiKeyFinder apiKeyFinder,
                           ApiKeyService apiKeyService) {
        this.mocking = mocking;
        this.apiKeyFinder = apiKeyFinder;
        this.apiKeyService = apiKeyService;
    }

    @PostConstruct
    public void ensureApiKeys() {
        if (!mocking || !this.apiKeyFinder.getAll().isEmpty()) {
            return;
        }

        LOGGER.info("Generating mock api keys...");

        for (ApiKeyType apiKeyType : ApiKeyType.values()) {
            if (apiKeyType != ApiKeyType.CLIENT) {
                ApiKeyToken apiKeyToken = ApiKeyToken.valueOf(apiKeyType.name() + "-super-secret-token");
                this.apiKeyService.create(
                        new ApiKeyDTO(
                                new ApiKeyId(),
                                new ApiKeyName(apiKeyToken.get() + "-mock"),
                                new TextDTO("", ""),
                                apiKeyToken,
                                apiKeyType
                        )
                );

                LOGGER.info("Api key of type " + apiKeyType.name() + " has been generated with token: " + apiKeyToken.get());
            }
        }
    }

}
