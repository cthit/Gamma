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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

@Service
public class AuthorityLevelFacade extends Facade {

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

    public void create(String name) {
        this.accessGuard.require(isAdmin());

        this.authorityLevelRepository.create(new AuthorityLevelName(name));
    }

    public void delete(String name) {
        this.accessGuard.require(isAdmin());

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
        this.accessGuard.require(isAdmin());

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
        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name)).orElseThrow();

        List<SuperGroup> newSuperGroups = new ArrayList<>(authorityLevel.superGroups());
        for (int i = 0; i < newSuperGroups.size(); i++) {
            if (newSuperGroups.get(i).id().value().equals(superGroupId)) {
                newSuperGroups.remove(i);
                break;
            }
        }

        this.authorityLevelRepository.save(authorityLevel.withSuperGroups(newSuperGroups));
    }

    public void removeSuperGroupPostFromAuthorityLevel(String name, UUID superGroupId, UUID postId) {
        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name)).orElseThrow();

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

    public void removeUserFromAuthorityLevel(String name, UUID userId) {
        AuthorityLevel authorityLevel = this.authorityLevelRepository.get(new AuthorityLevelName(name)).orElseThrow();

        List<User> newUsers = new ArrayList<>(authorityLevel.users());
        for (int i = 0; i < newUsers.size(); i++) {
            if (newUsers.get(i).id().value().equals(userId)) {
                newUsers.remove(i);
                break;
            }
        }

        this.authorityLevelRepository.save(authorityLevel.withUsers(newUsers));
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
