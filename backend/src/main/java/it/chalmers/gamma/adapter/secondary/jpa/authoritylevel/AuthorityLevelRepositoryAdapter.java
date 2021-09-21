package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.authoritylevel.AuthorityLevelRepository;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevel;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.supergroup.SuperGroup;
import it.chalmers.gamma.domain.user.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthorityLevelRepositoryAdapter implements AuthorityLevelRepository {

    private final AuthorityLevelJpaRepository repository;

    private final AuthorityPostJpaRepository authorityPostRepository;
    private final AuthoritySuperGroupJpaRepository authoritySuperGroupRepository;
    private final AuthorityUserJpaRepository authorityUserRepository;

    private final SuperGroupJpaRepository superGroupRepository;
    private final UserJpaRepository userRepository;
    private final PostJpaRepository postRepository;

    private final UserEntityConverter userEntityConverter;

    public AuthorityLevelRepositoryAdapter(AuthorityLevelJpaRepository repository,
                                           AuthorityPostJpaRepository authorityPostRepository,
                                           AuthoritySuperGroupJpaRepository authoritySuperGroupRepository,
                                           AuthorityUserJpaRepository authorityUserRepository,
                                           SuperGroupJpaRepository superGroupRepository,
                                           UserJpaRepository userRepository,
                                           PostJpaRepository postRepository,
                                           UserEntityConverter userEntityConverter) {
        this.repository = repository;
        this.authorityPostRepository = authorityPostRepository;
        this.authoritySuperGroupRepository = authoritySuperGroupRepository;
        this.authorityUserRepository = authorityUserRepository;
        this.superGroupRepository = superGroupRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userEntityConverter = userEntityConverter;
    }

    @Override
    public void create(AuthorityLevelName authorityLevelName) {
        repository.save(new AuthorityLevelEntity(authorityLevelName.getValue()));
    }

    @Override
    public void create(AuthorityLevel authorityLevel) {
        String name = authorityLevel.name().getValue();
        AuthorityLevelEntity authorityLevelEntity = new AuthorityLevelEntity(name);

        List<AuthorityUserEntity> users = authorityLevel.users().stream().map(user -> new AuthorityUserEntity(toEntity(user), authorityLevelEntity)).toList();
        List<AuthorityPostEntity> posts = authorityLevel.posts().stream().map(post -> new AuthorityPostEntity(toEntity(post.superGroup()), toEntity(post.post()), authorityLevelEntity)).toList();
        List<AuthoritySuperGroupEntity> superGroups = authorityLevel.superGroups().stream().map(superGroup -> new AuthoritySuperGroupEntity(toEntity(superGroup), authorityLevelEntity)).toList();

        authorityLevelEntity.setUsers(users);
        authorityLevelEntity.setPosts(posts);
        authorityLevelEntity.setSuperGroups(superGroups);

        repository.save(authorityLevelEntity);
    }

    @Override
    public void delete(AuthorityLevelName authorityLevelName) {
        repository.deleteById(authorityLevelName.getValue());
    }

    @Override
    public void save(AuthorityLevel authorityLevel) {
        AuthorityLevelName name = authorityLevel.name();
        //TODO: Convert authorityLevel to authorityLevelentity with repositories
//        repository.save(new AuthorityLevelEntity(
//                authorityLevel.name()
//        ));
    }

    @Override
    public List<AuthorityLevel> getAll() {
        return Collections.emptyList();
    }

    @Override
    public Optional<AuthorityLevel> get(AuthorityLevelName authorityLevelName) {
        return this.repository.findById(authorityLevelName.getValue()).map(authorityLevel -> authorityLevel.toDomain(this.userEntityConverter));
    }

    private UserEntity toEntity(User user) {
        return this.userRepository.getOne(user.id().getValue());
    }

    private PostEntity toEntity(Post post) {
        return this.postRepository.getOne(post.id().getValue());
    }

    private SuperGroupEntity toEntity(SuperGroup superGroup) {
        return this.superGroupRepository.getOne(superGroup.id().getValue());
    }

}
