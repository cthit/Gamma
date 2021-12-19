package it.chalmers.gamma.app.apikey;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class ApiKeyFacadeTest {

    @Mock
    private AccessGuard accessGuard;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @InjectMocks
    private ApiKeyFacade apiKeyFacade;

    /**
     * Creating a new type of api key should not be an easy decision.
     * Make sure that you really need to expose more data.
     * Note that adding a new api key type means that you probably need
     * to update your user agreement.
     */
    @Test
    public void EnsureThereIsOnlyFourApiKeyTypes() {
        String[] expected = new String[]{
                "CLIENT",
                "GOLDAPPS",
                "CHALMERSIT",
                "WHITELIST"
        };

        assertThat(apiKeyFacade.getApiKeyTypes())
                .isEqualTo(expected);
    }

    /**
     * Main test for create() that checks that isAdmin is called and that the generated token is also returned properly.
     */
    @Test
    public void Given_AValidApiKey_Expect_create_To_CreateValidApiKey() {
        ApiKeyFacade.NewApiKey newApiKey = new ApiKeyFacade.NewApiKey(
                "My Api Key",
                "Det här är en test api nyckel",
                "This is my test api key",
                "CLIENT"
        );

        String apiKeyTokenRaw = apiKeyFacade.create(newApiKey);

        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        verify(apiKeyRepository).save(captor.capture());
        ApiKey capturedApiKey = captor.getValue();

        //If not null, then they are valid.
        assertThat(capturedApiKey.id())
                .isNotNull();
        assertThat(capturedApiKey.apiKeyToken())
                .isNotNull();

        //Makes sure that the token returned is the same that is sent to the repository
        assertThat(apiKeyTokenRaw)
                .isEqualTo(capturedApiKey.apiKeyToken().value());

        ApiKey expectedApiKey = new ApiKey(
                capturedApiKey.id(),
                new PrettyName(newApiKey.prettyName()),
                new Text(
                        newApiKey.svDescription(),
                        newApiKey.enDescription()
                ),
                ApiKeyType.CLIENT,
                capturedApiKey.apiKeyToken()
        );


        assertThat(capturedApiKey)
                .isEqualTo(expectedApiKey);

        InOrder inOrder = inOrder(accessGuard, apiKeyRepository);

        //Makes sure that isAdmin is called first.
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(apiKeyRepository).save(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_NullPrettyName_Expect_create_To_Throw() {
        ApiKeyFacade.NewApiKey newApiKey = new ApiKeyFacade.NewApiKey(
                null,
                "Det här är en test api nyckel",
                "This is my test api key",
                "CLIENT"
        );

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> apiKeyFacade.create(newApiKey));
    }

    @Test
    public void Given_InvalidPrettyName_Expect_create_To_Throw() {
        ApiKeyFacade.NewApiKey newApiKey = new ApiKeyFacade.NewApiKey(
                "T",
                "Det här är en test api nyckel",
                "This is my test api key",
                "CLIENT"
        );

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> apiKeyFacade.create(newApiKey));
    }

    @Test
    public void Given_NullText_Expect_create_To_Throw() {
        ApiKeyFacade.NewApiKey newApiKey = new ApiKeyFacade.NewApiKey(
                "My Api Key",
                null,
                null,
                "CLIENT"
        );

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> apiKeyFacade.create(newApiKey));
    }

    @Test
    public void Given_InvalidApiKeyType_Expect_create_ToThrow() {
        ApiKeyFacade.NewApiKey newApiKey = new ApiKeyFacade.NewApiKey(
                "My Api Key",
                "Det här är en test api nyckel",
                "This is my test api key",
                "LMAO"
        );

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> apiKeyFacade.create(newApiKey));

        ApiKeyFacade.NewApiKey newApiKey2 = new ApiKeyFacade.NewApiKey(
                "My Api Key",
                "Det här är en test api nyckel",
                "This is my test api key",
                "client"
        );

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> apiKeyFacade.create(newApiKey));
    }

    @Test
    public void Given_AValidApiKeyId_Expect_delete_To_DeleteApiKey() throws ApiKeyRepository.ApiKeyNotFoundException {
        UUID id = UUID.randomUUID();

        apiKeyFacade.delete(id);

        InOrder inOrder = inOrder(accessGuard, apiKeyRepository);

        //Makes sure that isAdmin is called first.
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(apiKeyRepository).delete(new ApiKeyId(id));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_AValidApiKeyId_Expect_getById_To_ReturnApiKey() {
        UUID id = UUID.randomUUID();

        ApiKey apiKey = new ApiKey(
                new ApiKeyId(id),
                new PrettyName("My Api Key"),
                new Text(
                        "Det här är en test api nyckel",
                        "This is my test api key"
                ),
                ApiKeyType.CLIENT,
                ApiKeyToken.generate()
        );

        given(this.apiKeyRepository.getById(apiKey.id()))
                .willReturn(Optional.of(apiKey));

        Optional<ApiKeyFacade.ApiKeyDTO> maybeApiKey = apiKeyFacade.getById(id);

        ApiKeyDTOAssert.assertThat(maybeApiKey)
                .isEqualTo(apiKey);

        InOrder inOrder = inOrder(accessGuard, apiKeyRepository);

        //Makes sure that isAdmin is called first.
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(apiKeyRepository).getById(apiKey.id());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_InvalidApiKeyId_Expect_getById_To_ReturnEmptyOptional() {
        UUID id = UUID.randomUUID();
        ApiKeyId apiKeyId = new ApiKeyId(id);

        given(this.apiKeyRepository.getById(apiKeyId))
                .willReturn(Optional.empty());

        assertThat(this.apiKeyFacade.getById(id))
                .isEmpty();
    }

    @Test
    public void Given_ExistingApiKeys_Expect_getAll_To_ThoseApiKeys() {
        ApiKey apiKey1 = new ApiKey(
                ApiKeyId.generate(),
                new PrettyName("My Api Key"),
                new Text(
                        "Det här är en test api nyckel",
                        "This is my test api key"
                ),
                ApiKeyType.CLIENT,
                ApiKeyToken.generate()
        );

        ApiKey apiKey2 = new ApiKey(
                ApiKeyId.generate(),
                new PrettyName("My Api Key 2"),
                new Text(
                        "Det här är en test api nyckel 2",
                        "This is my test api key 2"
                ),
                ApiKeyType.CHALMERSIT,
                ApiKeyToken.generate()
        );

        List<ApiKey> apiKeys = List.of(apiKey1, apiKey2);

        given(this.apiKeyRepository.getAll())
                .willReturn(apiKeys);

        List<ApiKeyFacade.ApiKeyDTO> apiKeyDTOs = apiKeyFacade.getAll();

        assertThat(apiKeyDTOs.size())
                .isEqualTo(2);

        for (int i = 0; i < apiKeys.size(); i++) {
            ApiKeyFacade.ApiKeyDTO apiKeyDTO = apiKeyDTOs.get(i);
            ApiKey apiKey = apiKeys.get(i);

            ApiKeyDTOAssert.assertThat(apiKeyDTO)
                    .isEqualTo(apiKey);
        }

        InOrder inOrder = inOrder(accessGuard, apiKeyRepository);

        //Makes sure that isAdmin is called first.
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(apiKeyRepository).getAll();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_AValidApiKeyId_Expect_resetApiKeyToken_To_ResetSuccessfully() throws ApiKeyRepository.ApiKeyNotFoundException {
        ApiKeyId apiKeyId = ApiKeyId.generate();
        ApiKeyToken previousToken = ApiKeyToken.generate();
        ApiKey apiKey = new ApiKey(
                apiKeyId,
                new PrettyName("My Api Key"),
                new Text(
                        "Det här är en test api nyckel",
                        "This is my test api key"
                ),
                ApiKeyType.CLIENT,
                previousToken
        );

        given(this.apiKeyRepository.getById(apiKeyId))
                .willReturn(Optional.of(apiKey));

        String newToken = this.apiKeyFacade.resetApiKeyToken(apiKeyId.value());
        ApiKeyToken newApiKeyToken = new ApiKeyToken(newToken);

        ApiKey newApiKey = new ApiKey(
                apiKeyId,
                new PrettyName("My Api Key"),
                new Text(
                        "Det här är en test api nyckel",
                        "This is my test api key"
                ),
                ApiKeyType.CLIENT,
                newApiKeyToken
        );

        InOrder inOrder = inOrder(accessGuard, apiKeyRepository);

        //Makes sure that isAdmin is called first.
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(apiKeyRepository).getById(apiKeyId);
        inOrder.verify(apiKeyRepository).save(newApiKey);
        inOrder.verifyNoMoreInteractions();
    }

    public static class ApiKeyDTOAssert extends AbstractAssert<ApiKeyDTOAssert, ApiKeyFacade.ApiKeyDTO> {
        protected ApiKeyDTOAssert(ApiKeyFacade.ApiKeyDTO actual) {
            super(actual, ApiKeyDTOAssert.class);
        }

        public static ApiKeyDTOAssert assertThat(ApiKeyFacade.ApiKeyDTO actual) {
            return new ApiKeyDTOAssert(actual);
        }

        public static ApiKeyDTOAssert assertThat(Optional<ApiKeyFacade.ApiKeyDTO> actual) {
            if (actual.isEmpty()) {
                fail("Optional is empty");
                return null;
            }
            return new ApiKeyDTOAssert(actual.get());
        }

        public ApiKeyDTOAssert isEqualTo(ApiKey apiKey) {
            isNotNull();

            Assertions.assertThat(actual)
                    .hasOnlyFields("id", "prettyName", "svDescription", "enDescription", "keyType")
                    .isEqualTo(
                            new ApiKeyFacade.ApiKeyDTO(
                                    apiKey.id().value(),
                                    apiKey.prettyName().value(),
                                    apiKey.description().sv().value(),
                                    apiKey.description().en().value(),
                                    apiKey.keyType().name()
                            )
                    );

            return this;
        }

    }

}