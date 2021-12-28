package it.chalmers.gamma.app.authoritylevel;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isLocalRunner;

@Service
public class AuthorityLevelFacade extends Facade {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityLevelFacade.class);

    private final AuthorityLevelRepository authorityLevelRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SuperGroupRepository superGroupRepository;

    public AuthorityLevelFacade(AccessGuard accessGuard,
                                AuthorityLevelRepository authorityLevelRepository,
                                UserRepository userRepository,
                                PostRepository postRepository,
                                SuperGroupRepository superGroupRepository) {
        super(accessGuard);
        this.authorityLevelRepository = authorityLevelRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.superGroupRepository = superGroupRepository;
    }

    public void create(String name) throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        this.accessGuard.require(isAdmin());

        this.authorityLevelRepository.create(new AuthorityLevelName(name));
    }

    public void delete(String name) throws AuthorityLevelNotFoundException {
        this.accessGuard.require(isAdmin());

        try {
            this.authorityLevelRepository.delete(new AuthorityLevelName(name));
        } catch (AuthorityLevelRepository.AuthorityLevelNotFoundException e) {
            throw new AuthorityLevelNotFoundException();
        }
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
        this.accessGuard.require(isAdmin());

        return this.authorityLevelRepository.get(new AuthorityLevelName(name))
                .map(AuthorityLevelDTO::new);
    }

    public List<AuthorityLevelDTO> getAll() {
        this.accessGuard.require(isAdmin());

        return this.authorityLevelRepository.getAll()
                .stream()
                .map(AuthorityLevelDTO::new)
                .toList();
    }

    @Transactional
    public void addSuperGroupToAuthorityLevel(String name, UUID superGroupId)
            throws AuthorityLevelNotFoundException, SuperGroupNotFoundException {
        this.accessGuard.require(isAdmin());

        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name))
                .orElseThrow(AuthorityLevelNotFoundException::new);

        List<SuperGroup> superGroups = new ArrayList<>(authorityLevel.superGroups());
        superGroups.add(this.superGroupRepository.get(new SuperGroupId(superGroupId))
                .orElseThrow(SuperGroupNotFoundException::new));

        this.authorityLevelRepository.save(authorityLevel.withSuperGroups(superGroups));
    }

    @Transactional
    public void addSuperGroupPostToAuthorityLevel(String name, UUID superGroupId, UUID postId)
            throws AuthorityLevelNotFoundException, SuperGroupNotFoundException, PostNotFoundException {
        this.accessGuard.require(isAdmin());

        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name))
                .orElseThrow(AuthorityLevelNotFoundException::new);

        List<AuthorityLevel.SuperGroupPost> posts = new ArrayList<>(authorityLevel.posts());
        posts.add(new AuthorityLevel.SuperGroupPost(
                this.superGroupRepository.get(new SuperGroupId(superGroupId))
                        .orElseThrow(SuperGroupNotFoundException::new),
                this.postRepository.get(new PostId(postId))
                        .orElseThrow(PostNotFoundException::new)
        ));

        this.authorityLevelRepository.save(authorityLevel.withPosts(posts));
    }

    @Transactional
    public void addUserToAuthorityLevel(String name, UUID userId) throws AuthorityLevelRepository.AuthorityLevelNotFoundRuntimeException, AuthorityLevelNotFoundException, UserNotFoundException {
        this.accessGuard.requireEither(
                isAdmin(),
                isLocalRunner()
        );

        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name))
                .orElseThrow(AuthorityLevelNotFoundException::new);

        List<User> newUsersList = new ArrayList<>(authorityLevel.users());
        User newUser = this.userRepository.get(new UserId(userId))
                .orElseThrow(UserNotFoundException::new);
        newUsersList.add(newUser);

        this.authorityLevelRepository.save(authorityLevel.withUsers(newUsersList));
    }

    @Transactional
    public void removeSuperGroupFromAuthorityLevel(String name, UUID superGroupId)
            throws AuthorityLevelNotFoundException {
        this.accessGuard.require(isAdmin());

        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name))
                .orElseThrow(AuthorityLevelNotFoundException::new);

        List<SuperGroup> newSuperGroups = new ArrayList<>(authorityLevel.superGroups());
        for (int i = 0; i < newSuperGroups.size(); i++) {
            if (newSuperGroups.get(i).id().value().equals(superGroupId)) {
                newSuperGroups.remove(i);
                break;
            }
        }

        this.authorityLevelRepository.save(authorityLevel.withSuperGroups(newSuperGroups));
    }

    @Transactional
    public void removeSuperGroupPostFromAuthorityLevel(String name, UUID superGroupId, UUID postId)
            throws AuthorityLevelNotFoundException {
        this.accessGuard.require(isAdmin());

        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name))
                .orElseThrow(AuthorityLevelNotFoundException::new);

        List<AuthorityLevel.SuperGroupPost> newPosts = new ArrayList<>(authorityLevel.posts());
        for (int i = 0; i < newPosts.size(); i++) {
            AuthorityLevel.SuperGroupPost superGroupPost = newPosts.get(i);
            if (superGroupPost.post().id().value().equals(postId)
                    && superGroupPost.superGroup().id().value().equals(superGroupId)) {
                newPosts.remove(i);
                break;
            }
        }

        this.authorityLevelRepository.save(authorityLevel.withPosts(newPosts));
    }

    @Transactional
    public void removeUserFromAuthorityLevel(String name, UUID userId) throws AuthorityLevelNotFoundException {
        this.accessGuard.require(isAdmin());

        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name))
                .orElseThrow(AuthorityLevelNotFoundException::new);

        List<User> newUsers = new ArrayList<>(authorityLevel.users());
        for (int i = 0; i < newUsers.size(); i++) {
            if (newUsers.get(i).id().value().equals(userId)) {
                newUsers.remove(i);
                break;
            }
        }

        this.authorityLevelRepository.save(authorityLevel.withUsers(newUsers));
    }

    public static class AuthorityLevelNotFoundException extends Exception { }
    public static class SuperGroupNotFoundException extends Exception { }
    public static class UserNotFoundException extends Exception { }
    public static class PostNotFoundException extends Exception { }

}
