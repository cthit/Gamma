package it.chalmers.gamma.app.authoritylevel;

import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.UserFacade;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static it.chalmers.gamma.utils.DomainUtils.chair;
import static it.chalmers.gamma.utils.DomainUtils.digit;
import static it.chalmers.gamma.utils.DomainUtils.dragit;
import static it.chalmers.gamma.utils.DomainUtils.drawit;
import static it.chalmers.gamma.utils.DomainUtils.emeritus;
import static it.chalmers.gamma.utils.DomainUtils.prit;
import static it.chalmers.gamma.utils.DomainUtils.styrit;
import static it.chalmers.gamma.utils.DomainUtils.u1;
import static it.chalmers.gamma.utils.DomainUtils.u2;
import static it.chalmers.gamma.utils.DomainUtils.u3;
import static it.chalmers.gamma.utils.DomainUtils.u4;
import static it.chalmers.gamma.utils.DomainUtils.u5;
import static it.chalmers.gamma.utils.DomainUtils.u6;
import static it.chalmers.gamma.utils.DomainUtils.u8;
import static it.chalmers.gamma.utils.DomainUtils.u9;
import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isLocalRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.inOrder;
import static org.mockito.BDDMockito.verify;

@ExtendWith(SpringExtension.class)
class AuthorityLevelFacadeUnitTest {

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
    public void Given_AValidName_Expect_create_To_CreateValidAuthorityLevel() throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
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
    public void Given_AValidName_Expect_delete_To_DeleteAuthorityLevel()
            throws AuthorityLevelFacade.AuthorityLevelNotFoundException,
            AuthorityLevelRepository.AuthorityLevelNotFoundException {
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

    @Test
    public void Given_VariousInvalidNames_Expect_delete_To_Throw() throws AuthorityLevelRepository.AuthorityLevelNotFoundException {
        assertThatNullPointerException()
                .isThrownBy(() -> authorityLevelFacade.delete(null));

        //Too short
        assertThatIllegalArgumentException()
                .isThrownBy(() -> authorityLevelFacade.delete("a"));

        //Uppercase
        assertThatIllegalArgumentException()
                .isThrownBy(() -> authorityLevelFacade.delete("A"));

        //Too long
        assertThatIllegalArgumentException()
                .isThrownBy(() -> authorityLevelFacade.delete("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));

        //Illegal characters
        assertThatIllegalArgumentException()
                .isThrownBy(() -> authorityLevelFacade.delete("ö$a"));

        //Legal name, but doesn't exist

        doThrow(AuthorityLevelRepository.AuthorityLevelNotFoundException.class)
                .when(authorityLevelRepository).delete(any());

        assertThatExceptionOfType(AuthorityLevelFacade.AuthorityLevelNotFoundException.class)
                .isThrownBy(() -> authorityLevelFacade.delete("hello"));
    }

    @Test
    public void Given_AValidName_Expect_get_To_ReturnAuthorityLevel() {
        AuthorityLevel adminAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("admin"),
                List.of(
                        sgp(
                                styrit,
                                chair
                        )
                ),
                List.of(
                        digit
                ),
                List.of(u1, u2, u3)
        );

        given(authorityLevelRepository.get(new AuthorityLevelName("admin")))
                .willReturn(Optional.of(adminAuthorityLevel));

        Optional<AuthorityLevelFacade.AuthorityLevelDTO> authorityLevelDTO = authorityLevelFacade.get("admin");

        ArgumentCaptor<AuthorityLevelName> captor = ArgumentCaptor.forClass(AuthorityLevelName.class);
        verify(authorityLevelRepository).get(captor.capture());
        AuthorityLevelName authorityLevelName = captor.getValue();

        assertThat(authorityLevelName)
                .isEqualTo(new AuthorityLevelName("admin"));

        AuthorityLevelDTOAssert.assertThat(authorityLevelDTO)
                .isEqualTo(adminAuthorityLevel);

        InOrder inOrder = inOrder(accessGuard, authorityLevelRepository);

        //Makes sure that isAdmin is called first
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(authorityLevelRepository).get(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_VariousInvalidNames_Expect_get_To_Throw() {
        assertThatNullPointerException()
                .isThrownBy(() -> authorityLevelFacade.get(null));

        //Too short
        assertThatIllegalArgumentException()
                .isThrownBy(() -> authorityLevelFacade.get("a"));

        //Uppercase
        assertThatIllegalArgumentException()
                .isThrownBy(() -> authorityLevelFacade.get("A"));

        //Too long
        assertThatIllegalArgumentException()
                .isThrownBy(() -> authorityLevelFacade.get("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
    }

    @Test
    public void MakeSureGetAllWorksProperly() {
        AuthorityLevel adminAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("admin"),
                List.of(sgp(styrit, chair)),
                List.of(digit),
                List.of(u1, u2, u3)
        );

        AuthorityLevel matAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("mat"),
                List.of(sgp(digit, chair)),
                Collections.emptyList(),
                List.of(u5, u6)
        );

        AuthorityLevel bookitAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("bookit"),
                Collections.emptyList(),
                List.of(prit),
                List.of(u8, u9)
        );

        List<AuthorityLevel> authorityLevels = List.of(adminAuthorityLevel, matAuthorityLevel, bookitAuthorityLevel);

        given(authorityLevelRepository.getAll())
                .willReturn(authorityLevels);

        List<AuthorityLevelFacade.AuthorityLevelDTO> authorityLevelDTOS = this.authorityLevelFacade.getAll();

        Assertions.assertThat(authorityLevelDTOS)
                .zipSatisfy(authorityLevels, (actual, expected) ->
                        AuthorityLevelDTOAssert.assertThat(actual)
                                .isEqualTo(expected));

        InOrder inOrder = inOrder(accessGuard, authorityLevelRepository);

        //Makes sure that isAdmin is called first
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(authorityLevelRepository).getAll();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_EmptyAuthorityLevel_Expect_addSuperGroupToAuthorityLevel_To_Work() throws AuthorityLevelFacade.AuthorityLevelNotFoundException, AuthorityLevelFacade.SuperGroupNotFoundException {
        AuthorityLevel adminAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("admin"),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        given(authorityLevelRepository.get(new AuthorityLevelName("admin")))
                .willReturn(Optional.of(adminAuthorityLevel));

        given(superGroupRepository.get(digit.id()))
                .willReturn(Optional.of(digit));

        this.authorityLevelFacade.addSuperGroupToAuthorityLevel("admin", digit.id().value());

        ArgumentCaptor<AuthorityLevel> captor = ArgumentCaptor.forClass(AuthorityLevel.class);
        verify(authorityLevelRepository).save(captor.capture());
        AuthorityLevel newAuthorityLevel = captor.getValue();

        Assertions.assertThat(newAuthorityLevel)
                .isEqualTo(new AuthorityLevel(
                        new AuthorityLevelName("admin"),
                        Collections.emptyList(),
                        List.of(digit),
                        Collections.emptyList()
                ));

        InOrder inOrder = inOrder(accessGuard, authorityLevelRepository, superGroupRepository);

        //Makes sure that isAdmin is called first
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(authorityLevelRepository).get(new AuthorityLevelName("admin"));
        inOrder.verify(superGroupRepository).get(digit.id());
        inOrder.verify(authorityLevelRepository).save(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_FilledAuthorityLevel_Expect_addSuperGroupToAuthorityLevel_To_Work() throws AuthorityLevelFacade.AuthorityLevelNotFoundException, AuthorityLevelFacade.SuperGroupNotFoundException {
        AuthorityLevel adminAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("admin"),
                List.of(sgp(styrit, chair), sgp(emeritus, chair)),
                List.of(drawit, dragit),
                List.of(u2, u3, u4)
        );

        given(authorityLevelRepository.get(new AuthorityLevelName("admin")))
                .willReturn(Optional.of(adminAuthorityLevel));

        given(superGroupRepository.get(digit.id()))
                .willReturn(Optional.of(digit));

        this.authorityLevelFacade.addSuperGroupToAuthorityLevel("admin", digit.id().value());

        ArgumentCaptor<AuthorityLevel> captor = ArgumentCaptor.forClass(AuthorityLevel.class);
        verify(authorityLevelRepository).save(captor.capture());
        AuthorityLevel newAuthorityLevel = captor.getValue();

        Assertions.assertThat(newAuthorityLevel)
                .isEqualTo(new AuthorityLevel(
                        new AuthorityLevelName("admin"),
                        List.of(sgp(styrit, chair), sgp(emeritus, chair)),
                        List.of(drawit, dragit, digit),
                        List.of(u2, u3, u4)
                ));
    }

    @Test
    public void Given_EmptyAuthorityLevel_Expect_addSuperGroupPostToAuthorityLevel_To_Work() throws AuthorityLevelFacade.AuthorityLevelNotFoundException, AuthorityLevelFacade.PostNotFoundException, AuthorityLevelFacade.SuperGroupNotFoundException {
        AuthorityLevel adminAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("admin"),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        given(authorityLevelRepository.get(new AuthorityLevelName("admin")))
                .willReturn(Optional.of(adminAuthorityLevel));

        given(superGroupRepository.get(digit.id()))
                .willReturn(Optional.of(digit));

        given(postRepository.get(chair.id()))
                .willReturn(Optional.of(chair));

        this.authorityLevelFacade.addSuperGroupPostToAuthorityLevel(
                "admin",
                digit.id().value(),
                chair.id().value()
        );

        ArgumentCaptor<AuthorityLevel> captor = ArgumentCaptor.forClass(AuthorityLevel.class);
        verify(authorityLevelRepository).save(captor.capture());
        AuthorityLevel newAuthorityLevel = captor.getValue();

        Assertions.assertThat(newAuthorityLevel)
                .isEqualTo(new AuthorityLevel(
                        new AuthorityLevelName("admin"),
                        List.of(sgp(digit, chair)),
                        Collections.emptyList(),
                        Collections.emptyList()
                ));

        InOrder inOrder = inOrder(
                accessGuard,
                authorityLevelRepository,
                superGroupRepository,
                postRepository
        );

        //Makes sure that isAdmin is called first
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(authorityLevelRepository).get(new AuthorityLevelName("admin"));
        inOrder.verify(superGroupRepository).get(digit.id());
        inOrder.verify(postRepository).get(chair.id());
        inOrder.verify(authorityLevelRepository).save(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_FilledAuthorityLevel_Expect_addSuperGroupPostToAuthorityLevel_To_Work() throws AuthorityLevelFacade.AuthorityLevelNotFoundException, AuthorityLevelFacade.PostNotFoundException, AuthorityLevelFacade.SuperGroupNotFoundException {
        AuthorityLevel adminAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("admin"),
                List.of(sgp(styrit, chair), sgp(emeritus, chair)),
                List.of(drawit, dragit),
                List.of(u2, u3, u4)
        );

        given(authorityLevelRepository.get(new AuthorityLevelName("admin")))
                .willReturn(Optional.of(adminAuthorityLevel));

        given(superGroupRepository.get(digit.id()))
                .willReturn(Optional.of(digit));

        given(postRepository.get(chair.id()))
                .willReturn(Optional.of(chair));

        this.authorityLevelFacade.addSuperGroupPostToAuthorityLevel(
                "admin",
                digit.id().value(),
                chair.id().value()
        );

        ArgumentCaptor<AuthorityLevel> captor = ArgumentCaptor.forClass(AuthorityLevel.class);
        verify(authorityLevelRepository).save(captor.capture());
        AuthorityLevel newAuthorityLevel = captor.getValue();

        Assertions.assertThat(newAuthorityLevel)
                .isEqualTo(new AuthorityLevel(
                        new AuthorityLevelName("admin"),
                        List.of(sgp(styrit, chair), sgp(emeritus, chair), sgp(digit, chair)),
                        List.of(drawit, dragit),
                        List.of(u2, u3, u4)
                ));
    }

    @Test
    public void Given_EmptyAuthorityLevel_Expect_addUserToAuthorityLevel_To_Work() throws AuthorityLevelFacade.UserNotFoundException, AuthorityLevelFacade.AuthorityLevelNotFoundException {
        AuthorityLevel adminAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("admin"),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        given(authorityLevelRepository.get(new AuthorityLevelName("admin")))
                .willReturn(Optional.of(adminAuthorityLevel));

        given(userRepository.get(u1.id()))
                .willReturn(Optional.of(u1));

        this.authorityLevelFacade.addUserToAuthorityLevel("admin", u1.id().value());

        ArgumentCaptor<AuthorityLevel> captor = ArgumentCaptor.forClass(AuthorityLevel.class);
        verify(authorityLevelRepository).save(captor.capture());
        AuthorityLevel newAuthorityLevel = captor.getValue();

        Assertions.assertThat(newAuthorityLevel)
                .isEqualTo(new AuthorityLevel(
                        new AuthorityLevelName("admin"),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        List.of(u1)
                ));

        InOrder inOrder = inOrder(
                accessGuard,
                authorityLevelRepository,
                userRepository
        );

        //Makes sure that isAdmin is called first
        inOrder.verify(accessGuard).requireEither(isAdmin(), isLocalRunner());
        inOrder.verify(authorityLevelRepository).get(new AuthorityLevelName("admin"));
        inOrder.verify(userRepository).get(u1.id());
        inOrder.verify(authorityLevelRepository).save(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_FilledAuthorityLevel_Expect_addUserToAuthorityLevel_To_Work() throws AuthorityLevelFacade.UserNotFoundException, AuthorityLevelFacade.AuthorityLevelNotFoundException {
        AuthorityLevel adminAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("admin"),
                List.of(sgp(styrit, chair), sgp(emeritus, chair)),
                List.of(drawit, dragit),
                List.of(u2, u3, u4)
        );

        given(authorityLevelRepository.get(new AuthorityLevelName("admin")))
                .willReturn(Optional.of(adminAuthorityLevel));

        given(userRepository.get(u1.id()))
                .willReturn(Optional.of(u1));

        this.authorityLevelFacade.addUserToAuthorityLevel("admin", u1.id().value());

        ArgumentCaptor<AuthorityLevel> captor = ArgumentCaptor.forClass(AuthorityLevel.class);
        verify(authorityLevelRepository).save(captor.capture());
        AuthorityLevel newAuthorityLevel = captor.getValue();

        Assertions.assertThat(newAuthorityLevel)
                .isEqualTo(new AuthorityLevel(
                        new AuthorityLevelName("admin"),
                        List.of(sgp(styrit, chair), sgp(emeritus, chair)),
                        List.of(drawit, dragit),
                        List.of(u2, u3, u4, u1)
                ));
    }

    @Test
    public void Given_AuthorityLevelWithSuperGroup_Expect_removeSuperGroupFromAuthorityLevel_To_Work() throws AuthorityLevelFacade.AuthorityLevelNotFoundException {
        AuthorityLevel adminAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("admin"),
                Collections.emptyList(),
                List.of(digit),
                Collections.emptyList()
        );

        given(authorityLevelRepository.get(new AuthorityLevelName("admin")))
                .willReturn(Optional.of(adminAuthorityLevel));

        this.authorityLevelFacade.removeSuperGroupFromAuthorityLevel("admin", digit.id().value());

        ArgumentCaptor<AuthorityLevel> captor = ArgumentCaptor.forClass(AuthorityLevel.class);
        verify(authorityLevelRepository).save(captor.capture());
        AuthorityLevel newAuthorityLevel = captor.getValue();

        Assertions.assertThat(newAuthorityLevel)
                .isEqualTo(new AuthorityLevel(
                        new AuthorityLevelName("admin"),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                ));

        InOrder inOrder = inOrder(accessGuard, authorityLevelRepository, superGroupRepository);

        //Makes sure that isAdmin is called first
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(authorityLevelRepository).get(new AuthorityLevelName("admin"));
        inOrder.verify(authorityLevelRepository).save(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_AuthorityLevelWithSuperGroupPost_Expect_removeSuperGroupPostFromAuthorityLevel_To_Work() throws AuthorityLevelFacade.AuthorityLevelNotFoundException {
        AuthorityLevel adminAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("admin"),
                List.of(sgp(digit, chair)),
                Collections.emptyList(),
                Collections.emptyList()
        );

        given(authorityLevelRepository.get(new AuthorityLevelName("admin")))
                .willReturn(Optional.of(adminAuthorityLevel));

        this.authorityLevelFacade.removeSuperGroupPostFromAuthorityLevel("admin", digit.id().value(), chair.id().value());

        ArgumentCaptor<AuthorityLevel> captor = ArgumentCaptor.forClass(AuthorityLevel.class);
        verify(authorityLevelRepository).save(captor.capture());
        AuthorityLevel newAuthorityLevel = captor.getValue();

        Assertions.assertThat(newAuthorityLevel)
                .isEqualTo(new AuthorityLevel(
                        new AuthorityLevelName("admin"),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                ));

        InOrder inOrder = inOrder(accessGuard, authorityLevelRepository, superGroupRepository);

        //Makes sure that isAdmin is called first
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(authorityLevelRepository).get(new AuthorityLevelName("admin"));
        inOrder.verify(authorityLevelRepository).save(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void Given_AuthorityLevelWithUser_Expect_removeUserFromAuthorityLevel_To_Work() throws AuthorityLevelFacade.AuthorityLevelNotFoundException {
        AuthorityLevel adminAuthorityLevel = new AuthorityLevel(
                new AuthorityLevelName("admin"),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(u1)
        );

        given(authorityLevelRepository.get(new AuthorityLevelName("admin")))
                .willReturn(Optional.of(adminAuthorityLevel));

        this.authorityLevelFacade.removeUserFromAuthorityLevel("admin", u1.id().value());

        ArgumentCaptor<AuthorityLevel> captor = ArgumentCaptor.forClass(AuthorityLevel.class);
        verify(authorityLevelRepository).save(captor.capture());
        AuthorityLevel newAuthorityLevel = captor.getValue();

        Assertions.assertThat(newAuthorityLevel)
                .isEqualTo(new AuthorityLevel(
                        new AuthorityLevelName("admin"),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                ));

        InOrder inOrder = inOrder(accessGuard, authorityLevelRepository, superGroupRepository);

        //Makes sure that isAdmin is called first
        inOrder.verify(accessGuard).require(isAdmin());
        inOrder.verify(authorityLevelRepository).get(new AuthorityLevelName("admin"));
        inOrder.verify(authorityLevelRepository).save(any());
        inOrder.verifyNoMoreInteractions();
    }

    public static class AuthorityLevelDTOAssert extends AbstractAssert<AuthorityLevelDTOAssert, AuthorityLevelFacade.AuthorityLevelDTO> {

        protected AuthorityLevelDTOAssert(AuthorityLevelFacade.AuthorityLevelDTO actual) {
            super(actual, AuthorityLevelDTOAssert.class);
        }

        public static AuthorityLevelDTOAssert assertThat(AuthorityLevelFacade.AuthorityLevelDTO authorityLevelDTO) {
            return new AuthorityLevelDTOAssert(authorityLevelDTO);
        }

        public static AuthorityLevelDTOAssert assertThat(Optional<AuthorityLevelFacade.AuthorityLevelDTO> actual) {
            if (actual.isEmpty()) {
                fail("Optional is empty");
                return null;
            }
            return new AuthorityLevelDTOAssert(actual.get());
        }


        public AuthorityLevelDTOAssert isEqualTo(AuthorityLevel authorityLevel) {
            isNotNull();

            Assertions.assertThat(actual)
                    .hasOnlyFields("authorityLevelName", "superGroups", "users", "posts");

            Assertions.assertThat(actual.authorityLevelName())
                    .isEqualTo(authorityLevel.name().value());

            Assertions.assertThat(actual.superGroups()).zipSatisfy(authorityLevel.superGroups(), (actual, expected) ->
                    Assertions.assertThat(actual)
                            .isEqualTo(new SuperGroupFacade.SuperGroupDTO(expected))
            );

            Assertions.assertThat(actual.users()).zipSatisfy(authorityLevel.users(), (actual, expected) ->
                    Assertions.assertThat(actual)
                            .isEqualTo(new UserFacade.UserDTO(expected))
            );

            Assertions.assertThat(actual.posts()).zipSatisfy(authorityLevel.posts(), (actual, expected) -> {
                Assertions.assertThat(actual.superGroup())
                        .isEqualTo(new SuperGroupFacade.SuperGroupDTO(expected.superGroup()));
                Assertions.assertThat(actual.post())
                        .isEqualTo(new PostFacade.PostDTO(expected.post()));
            });

            return this;
        }
    }

    private static AuthorityLevel.SuperGroupPost sgp(SuperGroup sg, Post p) {
        return new AuthorityLevel.SuperGroupPost(sg, p);
    }

}