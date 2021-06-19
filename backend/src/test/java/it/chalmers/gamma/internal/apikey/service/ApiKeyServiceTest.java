package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.domain.ApiKey;
import it.chalmers.gamma.domain.ApiKeyId;
import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.domain.ApiKeyType;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.domain.Text;
import it.chalmers.gamma.domain.TextValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @InjectMocks
    private ApiKeyService apiKeyService;

    @Test
    void create() {
        ApiKey apiKey = new ApiKey(
                ApiKeyId.generate(),
                PrettyName.valueOf("myapikey"),
                new Text(
                        "Min API nyckel",
                        "My API key"
                ),
                ApiKeyType.CLIENT
        );

        apiKeyService.create(apiKey, ApiKeyToken.generate());

        ArgumentCaptor<ApiKeyEntity> captor = ArgumentCaptor.forClass(ApiKeyEntity.class);
        verify(apiKeyRepository).save(captor.capture());
        ApiKeyEntity newApiKey = captor.getValue();

        assertThat(apiKey)
                .usingRecursiveComparison()
                .isEqualTo(newApiKey);
    }

    @Test
    void delete() {
        ApiKeyId apiKeyId = ApiKeyId.generate();
        ApiKeyId apiKeyId2 = ApiKeyId.generate();

        willThrow(IllegalArgumentException.class)
                .given(apiKeyRepository)
                .deleteById(any(ApiKeyId.class));
        willDoNothing()
                .given(apiKeyRepository)
                .deleteById(apiKeyId);

        assertThatNoException()
                .isThrownBy(() -> apiKeyService.delete(apiKeyId));
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> apiKeyService.delete(apiKeyId2));
    }
}