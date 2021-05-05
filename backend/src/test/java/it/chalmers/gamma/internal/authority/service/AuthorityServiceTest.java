package it.chalmers.gamma.internal.authority.service;

import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthorityServiceTest {

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private AuthorityService authorityService;

    @Test
    void successfulCreation() {
        SuperGroupId superGroupId = new SuperGroupId();
        PostId postId = new PostId();
        AuthorityLevelName authorityLevelName = new AuthorityLevelName("authority");

        AuthorityShallowDTO authorityShallowDTO = new AuthorityShallowDTO(superGroupId, postId, authorityLevelName);

        assertThatNoException()
                .isThrownBy(() -> authorityService.create(authorityShallowDTO));

        ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository).save(captor.capture());
        Authority newAuthority = captor.getValue();

        assertThat(authorityShallowDTO)
                .usingRecursiveComparison()
                .isEqualTo(newAuthority.getId());
    }

    @Test
    void throwIfAuthorityAlreadyExists() {
        SuperGroupId superGroupId = new SuperGroupId();
        PostId postId = new PostId();
        AuthorityLevelName authorityLevelName = new AuthorityLevelName("authority");

        AuthorityShallowDTO authorityShallowDTO = new AuthorityShallowDTO(superGroupId, postId, authorityLevelName);
        Authority authority = AuthorityFactory.create(superGroupId, postId, authorityLevelName);

        willThrow(IllegalArgumentException.class)
                .given(authorityRepository)
                .save(authority);

        assertThatExceptionOfType(EntityAlreadyExistsException.class)
                .isThrownBy(() -> authorityService.create(authorityShallowDTO));
    }

    @Test
    void compareEntityAndDTO() {
        SuperGroupId superGroupId = new SuperGroupId();
        PostId postId = new PostId();
        AuthorityLevelName authorityLevelName = new AuthorityLevelName("authority");

        AuthorityShallowDTO authorityShallowDTO = new AuthorityShallowDTO(superGroupId, postId, authorityLevelName);

        Authority authority = AuthorityFactory.create(superGroupId, postId, authorityLevelName);

        assertThat(authority)
                .usingRecursiveComparison()
                .ignoringFields("id") //id is compared in successfulCreation
                .isEqualTo(authorityShallowDTO);
    }

    @Test
    void successfulDeletion() {
        AuthorityPK authority = new AuthorityPK(
                new SuperGroupId(),
                new PostId(),
                new AuthorityLevelName("authority1")
        );

        AuthorityPK authority2 = new AuthorityPK(
                new SuperGroupId(),
                new PostId(),
                new AuthorityLevelName("authority1")
        );

        willThrow(IllegalArgumentException.class)
                .given(authorityRepository)
                .deleteById(any(AuthorityPK.class));

        willDoNothing()
                .given(authorityRepository)
                .deleteById(authority);

        assertThatNoException()
                .isThrownBy(() -> authorityService.delete(authority));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> authorityService.delete(authority2));
    }

    @Test
    void failedDeletion() {
        AuthorityPK authority2 = new AuthorityPK(
                new SuperGroupId(),
                new PostId(),
                new AuthorityLevelName("authority1")
        );

        willThrow(IllegalArgumentException.class)
                .given(authorityRepository)
                .deleteById(authority2);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> authorityService.delete(authority2));
    }

}