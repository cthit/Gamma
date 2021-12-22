package it.chalmers.gamma.app.authoritylevel;

import it.chalmers.gamma.app.ListOfSuperGroupDTOAssert;
import it.chalmers.gamma.app.ListOfUserDTOAssert;
import it.chalmers.gamma.app.PostDTOAssert;
import it.chalmers.gamma.app.SuperGroupDTOAssert;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class AuthorityLevelFacadeTest {

    @Mock
    private AccessGuard accessGuard;

    @Mock
    private AuthorityLevelRepository authorityLevelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SuperGroupRepository superGroupRepository;

    @InjectMocks
    private AuthorityLevelFacade authorityLevelFacade;

    @Test
    public void Given_AValidName_Expect_create_To_CreateValidAuthorityLevel() {
        String adminAuthorityLevelName = "admin";

        authorityLevelFacade.create(adminAuthorityLevelName);

        ArgumentCaptor<AuthorityLevelName> captor = ArgumentCaptor.forClass(AuthorityLevelName.class);
        verify(authorityLevelRepository).create(captor.capture());
        AuthorityLevelName authorityLevelName = captor.getValue();

        assertThat(authorityLevelName)
                .isEqualTo(new AuthorityLevelName(adminAuthorityLevelName));

        InOrder inOrder = inOrder(accessGuard, authorityLevelRepository);

        //Makes sure that isAdmin is called first
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(authorityLevelRepository).create(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_VariousInvalidNames_Expect_create_To_Throw() {
        assertThatNullPointerException()
                .isThrownBy(() -> authorityLevelFacade.create(null));

        //Too short
        assertThatIllegalArgumentException()
                .isThrownBy(() -> authorityLevelFacade.create("a"));

        //Uppercase
        assertThatIllegalArgumentException()
                .isThrownBy(() -> authorityLevelFacade.create("A"));

        //Too long
        assertThatIllegalArgumentException()
                .isThrownBy(() -> authorityLevelFacade.create("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
    }

    @Test
    public void Given_AValidName_Expect_delete_To_DeleteAuthorityLevel() {
        String adminAuthorityLevelName = "admin";

        authorityLevelFacade.delete(adminAuthorityLevelName);

        ArgumentCaptor<AuthorityLevelName> captor = ArgumentCaptor.forClass(AuthorityLevelName.class);
        verify(authorityLevelRepository).delete(captor.capture());
        AuthorityLevelName authorityLevelName = captor.getValue();

        assertThat(authorityLevelName)
                .isEqualTo(new AuthorityLevelName(adminAuthorityLevelName));

        InOrder inOrder = inOrder(accessGuard, authorityLevelRepository);

        //Makes sure that isAdmin is called first
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(authorityLevelRepository).delete(any());
        inOrder.verifyNoMoreInteractions();
    }

    public static class AuthorityLevelDTOAssert extends AbstractAssert<AuthorityLevelDTOAssert, AuthorityLevelFacade.AuthorityLevelDTO> {

        protected AuthorityLevelDTOAssert(AuthorityLevelFacade.AuthorityLevelDTO actual) {
            super(actual, AuthorityLevelDTOAssert.class);
        }

        public static AuthorityLevelDTOAssert assertThat(AuthorityLevelFacade.AuthorityLevelDTO authorityLevelDTO) {
            return new AuthorityLevelDTOAssert(authorityLevelDTO);
        }

        public AuthorityLevelDTOAssert isEqualTo(AuthorityLevel authorityLevel) {
            isNotNull();

            Assertions.assertThat(actual)
                    .hasOnlyFields("authorityLevelName", "superGroups", "users", "posts");

            Assertions.assertThat(actual.authorityLevelName())
                    .isEqualTo(authorityLevel.name().value());

            ListOfSuperGroupDTOAssert.assertThat(actual.superGroups())
                    .hasSameElements(authorityLevel.superGroups());

            ListOfUserDTOAssert.assertThat(actual.users())
                    .hasSameElements(authorityLevel.users());

            for (int i = 0; i < actual.posts().size(); i++) {
                AuthorityLevelFacade.SuperGroupPostDTO superGroupPostDTO = actual.posts().get(i);
                AuthorityLevel.SuperGroupPost superGroupPost = authorityLevel.posts().get(i);

                SuperGroupDTOAssert.assertThat(superGroupPostDTO.superGroup())
                        .isEqualTo(superGroupPost.superGroup());
                PostDTOAssert.assertThat(superGroupPostDTO.post())
                        .isEqualTo(superGroupPost.post());
            }

            return this;
        }

    }

}