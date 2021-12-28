package it.chalmers.gamma.app.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@DataJpaTest
@Import({AuthorityLevelFacade.class,
        AuthorityLevelRepositoryAdapter.class,
        SuperGroupRepositoryAdapter.class,
        SuperGroupEntityConverter.class,
        AuthorityLevelEntityConverter.class,
        SuperGroupEntityConverter.class,
        UserEntityConverter.class,
        PostEntityConverter.class,
        UserRepositoryAdapter.class,
        PostRepositoryAdapter.class,
        SuperGroupRepositoryAdapter.class})
public class AuthorityLevelFacadeIntegrationTest {

    @MockBean
    private AccessGuard accessGuard;

    @Autowired
    private AuthorityLevelFacade authorityLevelFacade;

    @Test
    public void help() throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        authorityLevelFacade.create("hello");

        assertThatExceptionOfType(AuthorityLevelFacade.UserNotFoundException.class)
                .isThrownBy(() -> authorityLevelFacade.addUserToAuthorityLevel("hello", UUID.randomUUID()));
    }

}
