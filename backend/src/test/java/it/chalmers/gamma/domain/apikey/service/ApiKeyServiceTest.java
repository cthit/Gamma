package it.chalmers.gamma.domain.apikey.service;

import it.chalmers.gamma.domain.text.data.dto.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
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
        ApiKeyDTO apiKeyDTO = new ApiKeyDTO(
                new ApiKeyName("myapikey"),
                new TextDTO(
                        "Min API nyckel",
                        "My API key"
                ),
                new ApiKeyToken()
        );

        apiKeyService.create(apiKeyDTO);

        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        verify(apiKeyRepository).save(captor.capture());
        ApiKey newApiKey = captor.getValue();

        assertThat(apiKeyDTO)
                .usingRecursiveComparison()
                .isEqualTo(newApiKey);
    }

    @Test
    void delete() {
        ApiKeyId apiKeyId = new ApiKeyId();
        ApiKeyId apiKeyId2 = new ApiKeyId();

        willThrow(IllegalArgumentException.class)
                .given(apiKeyRepository)
                .deleteById(any(ApiKeyId.class));
        willDoNothing()
                .given(apiKeyRepository)
                .deleteById(apiKeyId);

        assertThatNoException()
                .isThrownBy(() -> apiKeyService.delete(apiKeyId));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> apiKeyService.delete(apiKeyId2));
    }
}