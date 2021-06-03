package it.chalmers.gamma.internal.authority.post.service;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.domain.SuperGroupId;
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
    private AuthorityPostRepository authorityPostRepository;

    @InjectMocks
    private AuthorityPostService authorityPostService;

    @Test
    void successfulCreation() {
        SuperGroupId superGroupId = new SuperGroupId();
        PostId postId = new PostId();
        AuthorityLevelName authorityLevelName = new AuthorityLevelName("authority");

        AuthorityPostShallowDTO authorityPostShallowDTO = new AuthorityPostShallowDTO(superGroupId, postId, authorityLevelName);

        assertThatNoException()
                .isThrownBy(() -> authorityPostService.create(authorityPostShallowDTO));

        ArgumentCaptor<AuthorityPostEntity> captor = ArgumentCaptor.forClass(AuthorityPostEntity.class);
        verify(authorityPostRepository).save(captor.capture());
        AuthorityPostEntity newAuthorityPost = captor.getValue();

        assertThat(authorityPostShallowDTO)
                .usingRecursiveComparison()
                .isEqualTo(newAuthorityPost.id());
    }

    @Test
    void throwIfAuthorityAlreadyExists() {
        SuperGroupId superGroupId = new SuperGroupId();
        PostId postId = new PostId();
        AuthorityLevelName authorityLevelName = new AuthorityLevelName("authority");

        AuthorityPostShallowDTO authorityPostShallowDTO = new AuthorityPostShallowDTO(superGroupId, postId, authorityLevelName);
        AuthorityPostEntity authorityPost = AuthorityFactory.create(superGroupId, postId, authorityLevelName);

        willThrow(IllegalArgumentException.class)
                .given(authorityPostRepository)
                .save(authorityPost);

        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> authorityPostService.create(authorityPostShallowDTO));
    }

    @Test
    void compareEntityAndDTO() {
        SuperGroupId superGroupId = new SuperGroupId();
        PostId postId = new PostId();
        AuthorityLevelName authorityLevelName = new AuthorityLevelName("authority");

        AuthorityPostShallowDTO authorityPostShallowDTO = new AuthorityPostShallowDTO(superGroupId, postId, authorityLevelName);

        AuthorityPostEntity authorityPost = AuthorityFactory.create(superGroupId, postId, authorityLevelName);

        assertThat(authorityPost)
                .usingRecursiveComparison()
                .ignoringFields("id") //id is compared in successfulCreation
                .isEqualTo(authorityPostShallowDTO);
    }

    @Test
    void successfulDeletion() {
        AuthorityPostPK authority = new AuthorityPostPK(
                new SuperGroupId(),
                new PostId(),
                new AuthorityLevelName("authority1")
        );

        AuthorityPostPK authority2 = new AuthorityPostPK(
                new SuperGroupId(),
                new PostId(),
                new AuthorityLevelName("authority1")
        );

        willThrow(IllegalArgumentException.class)
                .given(authorityPostRepository)
                .deleteById(any(AuthorityPostPK.class));

        willDoNothing()
                .given(authorityPostRepository)
                .deleteById(authority);

        assertThatNoException()
                .isThrownBy(() -> authorityPostService.delete(authority));
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> authorityPostService.delete(authority2));
    }

    @Test
    void failedDeletion() {
        AuthorityPostPK authority2 = new AuthorityPostPK(
                new SuperGroupId(),
                new PostId(),
                new AuthorityLevelName("authority1")
        );

        willThrow(IllegalArgumentException.class)
                .given(authorityPostRepository)
                .deleteById(authority2);

        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> authorityPostService.delete(authority2));
    }

}