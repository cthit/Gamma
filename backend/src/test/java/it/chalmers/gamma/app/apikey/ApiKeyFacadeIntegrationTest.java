package it.chalmers.gamma.app.apikey;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyRepositoryAdapter;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@DataJpaTest
@Import({ApiKeyFacade.class,
        ApiKeyRepositoryAdapter.class,
        ApiKeyEntityConverter.class})
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ApiKeyFacadeIntegrationTest {

    @MockBean
    private AccessGuard accessGuard;

    @Autowired
    private ApiKeyFacade apiKeyFacade;

    @Test
    public void Given_ValidNewApiKey_Expect_create_To_Work() {
        ApiKeyFacade.NewApiKey newApiKey = new ApiKeyFacade.NewApiKey(
                "My api key",
                "Svenska",
                "English",
                "INFO"
        );

        apiKeyFacade.create(newApiKey);

        List<ApiKeyFacade.ApiKeyDTO> apiKeys = apiKeyFacade.getAll();

        assertThat(apiKeys)
                .hasSize(1)
                .allSatisfy(apiKeyDTO -> {
                    assertThat(apiKeyDTO)
                            .isEqualTo(new ApiKeyFacade.ApiKeyDTO(
                                    apiKeyDTO.id(),
                                    newApiKey.prettyName(),
                                    newApiKey.svDescription(),
                                    newApiKey.enDescription(),
                                    "INFO"
                            ));
                });

        assertThat(apiKeyFacade.getById(apiKeys.get(0).id()))
                .isPresent();
    }

    @Test
    public void Given_TwoApiKeys_Expect_create_To_Work() {
        ApiKeyFacade.NewApiKey newApiKey = new ApiKeyFacade.NewApiKey(
                "My api key",
                "Svenska",
                "English",
                "INFO"
        );

        apiKeyFacade.create(newApiKey);
        apiKeyFacade.create(newApiKey);

        List<ApiKeyFacade.ApiKeyDTO> apiKeys = apiKeyFacade.getAll();

        assertThat(apiKeys)
                .hasSize(2);

        //Different id
        assertThat(apiKeys.get(0))
                .isNotEqualTo(apiKeys.get(1));
    }

    @Test
    public void Given_ValidApiKey_Expect_delete_To_Work() throws ApiKeyFacade.ApiKeyNotFoundException {
        ApiKeyFacade.NewApiKey newApiKey = new ApiKeyFacade.NewApiKey(
                "My api key",
                "Svenska",
                "English",
                "INFO"
        );

        apiKeyFacade.create(newApiKey);
        List<ApiKeyFacade.ApiKeyDTO> apiKeys = apiKeyFacade.getAll();
        assertThat(apiKeys)
                .hasSize(1);

        UUID id = apiKeys.get(0).id();
        apiKeyFacade.delete(id);
        assertThat(apiKeyFacade.getAll())
                .isEmpty();
    }

    @Test
    public void Given_NoApiKeys_Expect_delete_To_Throw() {
        assertThatExceptionOfType(ApiKeyFacade.ApiKeyNotFoundException.class)
                .isThrownBy(() -> apiKeyFacade.delete(UUID.randomUUID()));
    }

    @Test
    public void Given_ValidApiKey_Expect_resetApiKeyToken_To_Work() throws ApiKeyFacade.ApiKeyNotFoundException {
        ApiKeyFacade.NewApiKey newApiKey = new ApiKeyFacade.NewApiKey(
                "My api key",
                "Svenska",
                "English",
                "INFO"
        );

        String token = apiKeyFacade.create(newApiKey);
        List<ApiKeyFacade.ApiKeyDTO> apiKeys = apiKeyFacade.getAll();
        assertThat(apiKeys)
                .hasSize(1);

        UUID id = apiKeys.get(0).id();
        String newToken = apiKeyFacade.resetApiKeyToken(id);

        assertThat(token)
                .isNotNull();
        assertThat(newToken)
                .isNotNull();

        assertThat(token)
                .isNotEqualTo(newToken);
    }

}
