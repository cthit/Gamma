package it.chalmers.gamma.app.authoritylevel;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.group.GroupRepository;
import it.chalmers.gamma.app.post.PostRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupRepository;
import it.chalmers.gamma.app.user.UserRepository;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevel;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.user.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorityLevelFacade extends Facade {

    private final AuthorityLevelRepository authorityLevelRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PostRepository postRepository;
    private final SuperGroupRepository superGroupRepository;

    public AuthorityLevelFacade(AccessGuard accessGuard,
                                AuthorityLevelRepository authorityLevelRepository,
                                UserRepository userRepository,
                                GroupRepository groupRepository,
                                PostRepository postRepository,
                                SuperGroupRepository superGroupRepository) {
        super(accessGuard);
        this.authorityLevelRepository = authorityLevelRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.postRepository = postRepository;
        this.superGroupRepository = superGroupRepository;
    }

    public void create(AuthorityLevelName authorityLevelName) {
        accessGuard.requireIsAdmin();
        this.authorityLevelRepository.create(authorityLevelName);
    }

    public void delete(AuthorityLevelName authorityLevelName) {
        accessGuard.requireIsAdmin();
        this.authorityLevelRepository.delete(authorityLevelName);
    }

    public Optional<AuthorityLevel> get(AuthorityLevelName authorityLevelName) {
        return this.authorityLevelRepository.get(authorityLevelName);
    }

    public List<AuthorityLevel> getAll() {
        accessGuard.requireIsAdmin();
        return this.authorityLevelRepository.getAll();
    }


    public void addToAuthorityLevel(AuthorityLevelName authorityLevelName, SuperGroupId superGroupId) {
        throw new UnsupportedOperationException();
    }

    public void addToAuthorityLevel(AuthorityLevelName authorityLevelName, SuperGroupId superGroupId, PostId postId) {
        throw new UnsupportedOperationException();
    }

    public void addToAuthorityLevel(AuthorityLevelName authorityLevelName, UserId userId) {
        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(authorityLevelName).orElseThrow();
        authorityLevel.users().add(this.userRepository.get(userId));

    }

    public void removeFromAuthorityLevel(AuthorityLevelName name, SuperGroupId superGroupId) {
        throw new UnsupportedOperationException();
    }

    public void removeFromAuthorityLevel(AuthorityLevelName name, SuperGroupId superGroupId, PostId postId) {
        throw new UnsupportedOperationException();
    }

    public void removeFromAuthorityLevel(AuthorityLevelName name, UserId userId) {
        throw new UnsupportedOperationException();
    }

    public boolean authorityLevelUsed(AuthorityLevelName adminAuthorityLevel) {
        Optional<AuthorityLevel> maybeAuthorityLevel = this.authorityLevelRepository.get(adminAuthorityLevel);
        if (maybeAuthorityLevel.isEmpty()) {
            return false;
        }

        AuthorityLevel authorityLevel = maybeAuthorityLevel.get();

        return !authorityLevel.posts().isEmpty()
                || !authorityLevel.users().isEmpty()
                || !authorityLevel.superGroups().isEmpty();
    }

    public void create(AuthorityLevel authorityLevel) {
        this.authorityLevelRepository.create(authorityLevel);
    }
}
