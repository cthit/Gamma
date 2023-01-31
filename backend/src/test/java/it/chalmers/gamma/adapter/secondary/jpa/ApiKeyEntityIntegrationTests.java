package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyRepositoryAdapter;
import it.chalmers.gamma.app.apikey.domain.*;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@Import({ApiKeyRepositoryAdapter.class, ApiKeyEntityConverter.class})
class ApiKeyEntityIntegrationTests extends AbstractEntityIntegrationTests {

    @Autowired
    private ApiKeyRepositoryAdapter apiKeyRepositoryAdapter;

    @Autowired
    private ApiKeyJpaRepository apiKeyJpaRepository;

    @Autowired
    private ApiKeyEntityConverter apiKeyEntityConverter;

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void Given_TwoApiKeys_Expect_create_To_Work() throws ApiKeyRepository.ApiKeyAlreadyExistRuntimeException {
        apiKeyRepositoryAdapter.create(
                new ApiKey(
                        ApiKeyId.generate(),
                        new PrettyName("what"),
                        new Text(
                                "lmao",
                                "lmao"
                        ),
                        ApiKeyType.CLIENT,
                        ApiKeyToken.generate()
                )
        );

        apiKeyRepositoryAdapter.create(
                new ApiKey(
                        ApiKeyId.generate(),
                        new PrettyName("what"),
                        new Text(
                                "lmao",
                                "lmao"
                        ),
                        ApiKeyType.CLIENT,
                        ApiKeyToken.generate()
                )
        );

        assertThat(apiKeyRepositoryAdapter.getAll())
                .hasSize(2);
    }

    @Test
    public void Given_TwoApiKeyWithSameId_Expect_create_To_Throw() throws ApiKeyRepository.ApiKeyAlreadyExistRuntimeException {
        ApiKeyId id = ApiKeyId.generate();

        apiKeyRepositoryAdapter.create(
                new ApiKey(
                        id,
                        new PrettyName("what"),
                        new Text(
                                "lmao",
                                "lmao"
                        ),
                        ApiKeyType.CLIENT,
                        ApiKeyToken.generate()
                )
        );

        assertThatExceptionOfType(ApiKeyRepository.ApiKeyAlreadyExistRuntimeException.class)
                .isThrownBy(() -> apiKeyRepositoryAdapter.create(
                        new ApiKey(
                                id,
                                new PrettyName("what"),
                                new Text(
                                        "lmao",
                                        "lmao"
                                ),
                                ApiKeyType.CLIENT,
                                ApiKeyToken.generate()
                        )
                ));

        assertThat(apiKeyRepositoryAdapter.getAll())
                .hasSize(1);
    }

    @Test
    public void Given_ApiKey_Expect_delete_To_Work()
            throws ApiKeyRepository.ApiKeyAlreadyExistRuntimeException, ApiKeyRepository.ApiKeyNotFoundException {
        ApiKeyId id = ApiKeyId.generate();

        apiKeyRepositoryAdapter.create(
                new ApiKey(
                        id,
                        new PrettyName("what"),
                        new Text(
                                "lmao",
                                "lmao"
                        ),
                        ApiKeyType.CLIENT,
                        ApiKeyToken.generate()
                )
        );

        assertThat(apiKeyRepositoryAdapter.getAll())
                .hasSize(1);

        apiKeyRepositoryAdapter.delete(id);

        assertThat(apiKeyRepositoryAdapter.getAll())
                .hasSize(0);
    }

    @Test
    public void Given_NoApiKeys_Expect_delete_To_Throw() {
        assertThatExceptionOfType(ApiKeyRepository.ApiKeyNotFoundException.class)
                .isThrownBy(() -> apiKeyRepositoryAdapter.delete(ApiKeyId.generate()));
    }

    @Test
    public void Given_NoApiKeys_Expect_resetApiKeyToken_To_Throw() {
        assertThatExceptionOfType(ApiKeyRepository.ApiKeyNotFoundException.class)
                .isThrownBy(() -> apiKeyRepositoryAdapter.resetApiKeyToken(ApiKeyId.generate()));
    }

    @Test
    public void Given_AValidApiKey_Expect_resetApiKeyToken_To_Work()
            throws ApiKeyRepository.ApiKeyAlreadyExistRuntimeException, ApiKeyRepository.ApiKeyNotFoundException {
        ApiKeyId id = ApiKeyId.generate();
        ApiKeyToken token = ApiKeyToken.generate();

        apiKeyRepositoryAdapter.create(
                new ApiKey(
                        id,
                        new PrettyName("what"),
                        new Text(
                                "lmao",
                                "lmao"
                        ),
                        ApiKeyType.CLIENT,
                        token
                )
        );

        ApiKeyToken newToken = this.apiKeyRepositoryAdapter.resetApiKeyToken(id);

        assertThat(newToken)
                .isNotNull()
                .isNotEqualTo(token);

        //Make sure that if we get the api key again, that the token is the same.
        ApiKey apiKey = this.apiKeyRepositoryAdapter.getById(id).orElse(null);
        assertThat(apiKey)
                .isNotNull()
                .extracting(ApiKey::apiKeyToken)
                .isEqualTo(newToken);
    }

    @Test
    public void Given_NoApiKeys_Expect_getAll_To_BeEmpty() {
        assertThat(apiKeyRepositoryAdapter.getAll())
                .isEmpty();
    }

    @Test
    public void Given_NoApiKeys_Expect_getById_To_BeEmpty() {
        assertThat(apiKeyRepositoryAdapter.getById(ApiKeyId.generate()))
                .isEmpty();
    }

    @Test
    public void Given_NoApiKeys_Expect_getByToken_To_BeEmpty() {
        assertThat(apiKeyRepositoryAdapter.getByToken(ApiKeyToken.generate()))
                .isEmpty();
    }

    @Test
    public void Given_AValidApiKey_Expect_getByToken_To_Work() throws ApiKeyRepository.ApiKeyAlreadyExistRuntimeException {
        ApiKeyToken token = ApiKeyToken.generate();
        ApiKey apiKey = new ApiKey(
                ApiKeyId.generate(),
                new PrettyName("what"),
                new Text(
                        "lmao",
                        "lmao"
                ),
                ApiKeyType.CLIENT,
                token
        );

        apiKeyRepositoryAdapter.create(apiKey);

        assertThat(apiKeyRepositoryAdapter.getByToken(token))
                .isNotEmpty()
                .get().isEqualTo(apiKey);
    }

    @Test
    public void Given_AApiKey_Expect_toDomain_To_Work() throws ApiKeyRepository.ApiKeyAlreadyExistRuntimeException {
        ApiKey apiKey = new ApiKey(
                ApiKeyId.generate(),
                new PrettyName("what"),
                new Text(
                        "lmao",
                        "lmao"
                ),
                ApiKeyType.CLIENT,
                ApiKeyToken.generate()
        );

        this.apiKeyRepositoryAdapter.create(apiKey);

        ApiKeyEntity retrievedEntity = this.apiKeyJpaRepository.getById(apiKey.id().value());
        ApiKey converted = this.apiKeyEntityConverter.toDomain(retrievedEntity);

        assertThat(converted)
                .isEqualTo(apiKey);
    }

}