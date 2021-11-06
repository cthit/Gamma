package it.chalmers.gamma.app.bootstrap;

import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevel;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.post.PostId;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.app.repository.AuthorityLevelRepository;
import it.chalmers.gamma.app.repository.PostRepository;
import it.chalmers.gamma.app.repository.SuperGroupRepository;
import it.chalmers.gamma.app.repository.UserRepository;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthorityLevelBootstrap {

    private static final Logger LOGGER = Logger.getLogger(AuthorityLevelBootstrap.class);

    private final AuthorityLevelRepository authorityLevelRepository;
    private final SuperGroupRepository superGroupRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MockData mockData;
    private final boolean mocking;

    public AuthorityLevelBootstrap(AuthorityLevelRepository authorityLevelRepository,
                                   SuperGroupRepository superGroupRepository,
                                   PostRepository postRepository,
                                   UserRepository userRepository,
                                   MockData mockData,
                                   @Value("${application.mocking}") boolean mocking) {
        this.authorityLevelRepository = authorityLevelRepository;
        this.superGroupRepository = superGroupRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.mockData = mockData;
        this.mocking = mocking;
    }

    public void createAuthorities() {
        //!= 1 implies that admin isn't the only authority level
        if (!this.mocking || this.authorityLevelRepository.getAll().size() != 1) {
            return;
        }

        LOGGER.info("========== AUTHORITY BOOTSTRAP ==========");

        Map<AuthorityLevelName, Authorities> authorityLevelMap = new HashMap<>();

        this.mockData.users().forEach(mockUser -> {
            if (mockUser.authorities() != null) {
                mockUser.authorities().forEach(name -> {
                    AuthorityLevelName authorityLevelName = new AuthorityLevelName(name);
                    ensureAuthorityLevelNameInMap(authorityLevelMap, authorityLevelName);

                    authorityLevelMap.get(authorityLevelName).users.add(new UserId(mockUser.id()));
                });
            }
        });

        this.mockData.superGroups().forEach(mockSuperGroup -> {
            if (mockSuperGroup.authorities() != null) {
                mockSuperGroup.authorities().forEach(name -> {
                    AuthorityLevelName authorityLevelName = new AuthorityLevelName(name);
                    ensureAuthorityLevelNameInMap(authorityLevelMap, authorityLevelName);

                    authorityLevelMap.get(authorityLevelName).superGroups.add(new SuperGroupId(mockSuperGroup.id()));
                });
            }
        });

        this.mockData.postAuthorities().forEach(mockPostAuthority -> {
            AuthorityLevelName authorityLevelName = new AuthorityLevelName(mockPostAuthority.name());
            ensureAuthorityLevelNameInMap(authorityLevelMap, authorityLevelName);

            authorityLevelMap.get(authorityLevelName).posts.add(new Authorities.SuperGroupPost(
                    new PostId(mockPostAuthority.postId()),
                    new SuperGroupId(mockPostAuthority.superGroupId())
            ));
        });

        // Make sure admin user isn't overwritten
        if (authorityLevelMap.containsKey(new AuthorityLevelName("admin"))) {
            User adminUser = this.userRepository.get(new Cid("admin")).orElseThrow();
            authorityLevelMap.get(new AuthorityLevelName("admin")).users.add(adminUser.id());
        }

        authorityLevelMap.forEach((authorityLevelName, authorities) -> this.authorityLevelRepository.save(
                new AuthorityLevel(
                        authorityLevelName,
                        authorities.posts
                                .stream()
                                .map(superGroupPost -> new AuthorityLevel.SuperGroupPost(
                                        this.superGroupRepository.get(superGroupPost.superGroupId).orElseThrow(),
                                        this.postRepository.get(superGroupPost.postId).orElseThrow()
                                )).toList(),
                        authorities.superGroups
                                .stream()
                                .map(superGroupId -> this.superGroupRepository.get(superGroupId).orElseThrow())
                                .toList(),
                        authorities.users
                                .stream()
                                .map(userId -> this.userRepository.get(userId).orElseThrow())
                                .toList()
                )
        ));

        LOGGER.info("==========                     ==========");
    }

    public static class Authorities {

        public record SuperGroupPost(PostId postId, SuperGroupId superGroupId) { }

        public List<SuperGroupId> superGroups;
        public List<UserId> users;
        public List<SuperGroupPost> posts;

        public Authorities() {
            this.superGroups = new ArrayList<>();
            this.users = new ArrayList<>();
            this.posts = new ArrayList<>();
        }
    }


    private void ensureAuthorityLevelNameInMap(Map<AuthorityLevelName, Authorities> authorityLevelMap, AuthorityLevelName authorityLevelName) {
        if (!authorityLevelMap.containsKey(authorityLevelName)) {
            authorityLevelMap.put(authorityLevelName, new Authorities());
        }
    }

}
