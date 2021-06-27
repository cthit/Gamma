package it.chalmers.gamma.app.apikey.service;

import it.chalmers.gamma.app.apikey.ApiKeyRepository;
import it.chalmers.gamma.app.domain.ApiKey;
import it.chalmers.gamma.app.domain.ApiKeyId;
import it.chalmers.gamma.app.domain.ApiKeyToken;
import it.chalmers.gamma.app.domain.ApiKeyType;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.app.domain.Text;
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
//
//    @Mock
//    private ApiKeyRepository apiKeyRepository;
//
//    @InjectMocks
//    private ApiKeyRepository apiKeyService;
//
//    @Test
//    void create() {
//        ApiKey apiKey = new ApiKey(
//                ApiKeyId.generate(),
//                PrettyName.valueOf("myapikey"),
//                new Text(
//                        "Min API nyckel",
//                        "My API key"
//                ),
//                ApiKeyType.CLIENT,
//                ApiKeyToken.generate()
//        );
//
//        apiKeyService.create(apiKey);
//
//        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
//        verify(apiKeyRepository).create(captor.capture());
//        ApiKey newApiKey = captor.getValue();
//
//        assertThat(apiKey)
//                .usingRecursiveComparison()
//                .isEqualTo(newApiKey);
//    }
//
//    @Test
//    void delete() {
//        ApiKeyId apiKeyId = ApiKeyId.generate();
//        ApiKeyId apiKeyId2 = ApiKeyId.generate();
//
//        willThrow(IllegalArgumentException.class)
//                .given(apiKeyRepository)
//                .delete(any(ApiKeyId.class));
//        willDoNothing()
//                .given(apiKeyRepository)
//                .delete(apiKeyId);
//
//        assertThatNoException()
//                .isThrownBy(() -> apiKeyService.delete(apiKeyId));
//        assertThatExceptionOfType(Exception.class)
//                .isThrownBy(() -> apiKeyService.delete(apiKeyId2));
//    }
}