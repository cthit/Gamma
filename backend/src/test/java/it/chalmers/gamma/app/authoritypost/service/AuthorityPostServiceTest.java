package it.chalmers.gamma.app.authoritypost.service;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityPostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityPostPK;
import it.chalmers.gamma.adapter.secondary.jpa.authoritypost.AuthorityPostJpaRepository;
import it.chalmers.gamma.app.authority.AuthorityPostService;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.domain.SuperGroupId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthorityPostServiceTest {

    @Mock
    private AuthorityPostJpaRepository authorityPostJpaRepository;

    @InjectMocks
    private AuthorityPostService authorityPostService;

    @Test
    void successfulCreation() {
        SuperGroupId superGroupId = SuperGroupId.generate();
        PostId postId = PostId.generate();
        AuthorityLevelName authorityLevelName = AuthorityLevelName.valueOf("name");

        AuthorityPostDTO authorityPostDTO = new AuthorityPostDTO(superGroupId, postId, authorityLevelName);

        assertThatNoException()
                .isThrownBy(() -> authorityPostService.create(authorityPostDTO));

        ArgumentCaptor<AuthorityPostEntity> captor = ArgumentCaptor.forClass(AuthorityPostEntity.class);
        verify(authorityPostJpaRepository).save(captor.capture());
        AuthorityPostEntity newAuthorityPost = captor.getValue();

        assertThat(authorityPostDTO)
                .usingRecursiveComparison()
                .isEqualTo(newAuthorityPost.id());
    }

    @Test
    void throwIfAuthorityAlreadyExists() {
        SuperGroupId superGroupId =  SuperGroupId.generate();
        PostId postId = PostId.generate();
        AuthorityLevelName authorityLevelName = AuthorityLevelName.valueOf("name");

        AuthorityPostDTO authorityPostDTO = new AuthorityPostDTO(superGroupId, postId, authorityLevelName);
        AuthorityPostEntity authorityPost = AuthorityFactory.create(superGroupId, postId, authorityLevelName);

        willThrow(IllegalArgumentException.class)
                .given(authorityPostJpaRepository)
                .save(authorityPost);

        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> authorityPostService.create(authorityPostDTO));
    }

    @Test
    void compareEntityAndDTO() {
        SuperGroupId superGroupId = SuperGroupId.generate();
        PostId postId = PostId.generate();
        AuthorityLevelName authorityLevelName = AuthorityLevelName.valueOf("name");

        AuthorityPostDTO authorityPostDTO = new AuthorityPostDTO(superGroupId, postId, authorityLevelName);

        AuthorityPostEntity authorityPost = AuthorityFactory.create(superGroupId, postId, authorityLevelName);

        assertThat(authorityPost)
                .usingRecursiveComparison()
                .ignoringFields("id") //id is compared in successfulCreation
                .isEqualTo(authorityPostDTO);
    }

    @Test
    void successfulDeletion() {
        AuthorityPostPK authority = new AuthorityPostPK(
                SuperGroupId.generate(),
                PostId.generate(),
                AuthorityLevelName.valueOf("authority1")
        );

        AuthorityPostPK authority2 = new AuthorityPostPK(
                SuperGroupId.generate(),
                PostId.generate(),
                AuthorityLevelName.valueOf("authority1")
        );

        willThrow(IllegalArgumentException.class)
                .given(authorityPostJpaRepository)
                .deleteById(any(AuthorityPostPK.class));

        willDoNothing()
                .given(authorityPostJpaRepository)
                .deleteById(authority);

        assertThatNoException()
                .isThrownBy(() -> authorityPostService.delete(authority));
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> authorityPostService.delete(authority2));
    }

    @Test
    void failedDeletion() {
        AuthorityPostPK authority2 = new AuthorityPostPK(
                SuperGroupId.generate(),
                PostId.generate(),
                AuthorityLevelName.valueOf("authority1")
        );

        willThrow(IllegalArgumentException.class)
                .given(authorityPostJpaRepository)
                .deleteById(authority2);

        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> authorityPostService.delete(authority2));
    }

}