package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.port.repository.AuthorityLevelRepository;
import it.chalmers.gamma.app.port.repository.GroupRepository;
import it.chalmers.gamma.app.port.repository.PostRepository;
import it.chalmers.gamma.app.port.repository.SuperGroupRepository;
import it.chalmers.gamma.app.port.repository.UserRepository;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevel;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.post.PostId;
import it.chalmers.gamma.app.domain.supergroup.SuperGroup;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.app.port.service.ImageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorityLevelFacade extends Facade {

    private final AuthorityLevelRepository authorityLevelRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PostRepository postRepository;
    private final SuperGroupRepository superGroupRepository;
    private final ImageService imageService;

    public AuthorityLevelFacade(AccessGuard accessGuard,
                                AuthorityLevelRepository authorityLevelRepository,
                                UserRepository userRepository,
                                GroupRepository groupRepository,
                                PostRepository postRepository,
                                SuperGroupRepository superGroupRepository,
                                ImageService imageService) {
        super(accessGuard);
        this.authorityLevelRepository = authorityLevelRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.postRepository = postRepository;
        this.superGroupRepository = superGroupRepository;
        this.imageService = imageService;
    }

    public void create(String name) {
        accessGuard.requireIsAdmin();
        this.authorityLevelRepository.create(new AuthorityLevelName(name));
    }

    public void delete(String name) {
        accessGuard.requireIsAdmin();
        this.authorityLevelRepository.delete(new AuthorityLevelName(name));
    }

    public record SuperGroupPostDTO(SuperGroupFacade.SuperGroupDTO superGroup, PostFacade.PostDTO post) {
        public SuperGroupPostDTO(AuthorityLevel.SuperGroupPost post) {
            this(new SuperGroupFacade.SuperGroupDTO(post.superGroup()), new PostFacade.PostDTO(post.post()));
        }
    }

    public record AuthorityLevelDTO(
            String authorityLevelName,
            List<SuperGroupFacade.SuperGroupDTO> superGroups,
            List<UserFacade.UserDTO> users,
            List<SuperGroupPostDTO> posts) {
        public AuthorityLevelDTO(AuthorityLevel authorityLevel) {
            this(authorityLevel.name().value(),
                    authorityLevel.superGroups().stream().map(SuperGroupFacade.SuperGroupDTO::new).toList(),
                    authorityLevel.users().stream().map(UserFacade.UserDTO::new).toList(),
                    authorityLevel.posts().stream().map(SuperGroupPostDTO::new).toList());
        }
    }


    public Optional<AuthorityLevelDTO> get(String name) {
        return this.authorityLevelRepository.get(new AuthorityLevelName(name)).map(AuthorityLevelDTO::new);
    }

    public List<AuthorityLevelDTO> getAll() {
        accessGuard.requireIsAdmin();
        return this.authorityLevelRepository.getAll()
                .stream()
                .map(AuthorityLevelDTO::new)
                .toList();
    }


    public void addSuperGroupToAuthorityLevel(String name, UUID superGroupId) {
        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name)).orElseThrow();

        List<SuperGroup> superGroups = new ArrayList<>(authorityLevel.superGroups());
        superGroups.add(this.superGroupRepository.get(new SuperGroupId(superGroupId)).orElseThrow());

        this.authorityLevelRepository.save(authorityLevel.withSuperGroups(superGroups));
    }

    public void addSuperGroupPostToAuthorityLevel(String name, UUID superGroupId, UUID postId) {
        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name)).orElseThrow();

        List<AuthorityLevel.SuperGroupPost> posts = new ArrayList<>(authorityLevel.posts());
        posts.add(new AuthorityLevel.SuperGroupPost(
                this.superGroupRepository.get(new SuperGroupId(superGroupId)).orElseThrow(),
                this.postRepository.get(new PostId(postId)).orElseThrow()
        ));

        this.authorityLevelRepository.save(authorityLevel.withPosts(posts));
    }

    public void addUserToAuthorityLevel(String name, UUID userId) {
        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name)).orElseThrow();

        List<User> newUsersList = new ArrayList<>(authorityLevel.users());
        newUsersList.add(this.userRepository.get(new UserId(userId)).orElseThrow());

        this.authorityLevelRepository.save(authorityLevel.withUsers(newUsersList));
    }

    public void removeSuperGroupFromAuthorityLevel(String name, UUID superGroupId) {
        throw new UnsupportedOperationException();
    }

    public void removeSuperGroupPostFromAuthorityLevel(String name, UUID superGroupId, UUID postId) {
        throw new UnsupportedOperationException();
    }

    public void removeUserFromAuthorityLevel(String name, UUID userId) {
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
        this.authorityLevelRepository.save(authorityLevel);
    }
}
