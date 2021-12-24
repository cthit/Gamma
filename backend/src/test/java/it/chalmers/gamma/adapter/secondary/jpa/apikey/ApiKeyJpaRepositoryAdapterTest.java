package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@DirtiesContext
@Import({ApiKeyJpaRepositoryAdapter.class, ApiKeyEntityConverter.class})
class ApiKeyJpaRepositoryAdapterTest {

    @Autowired
    private ApiKeyJpaRepositoryAdapter apiKeyJpaRepositoryAdapter;

    @Autowired
    private ApiKeyEntityConverter apiKeyEntityConverter;

    @Test
    public void test() {
        ApiKeyId id = ApiKeyId.generate();

        apiKeyJpaRepositoryAdapter.create(
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

        apiKeyJpaRepositoryAdapter.create(
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

        assertThat(apiKeyJpaRepositoryAdapter.getAll())
                .hasSize(1);
    }

    @Test
    public void test2() {
        apiKeyJpaRepositoryAdapter.create(
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
        apiKeyJpaRepositoryAdapter.create(
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

        assertThat(apiKeyJpaRepositoryAdapter.getAll())
                .hasSize(2);
    }

}