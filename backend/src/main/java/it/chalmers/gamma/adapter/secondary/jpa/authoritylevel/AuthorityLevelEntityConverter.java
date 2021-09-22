package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevel;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.supergroup.SuperGroup;
import it.chalmers.gamma.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorityLevelEntityConverter {

    private final UserEntityConverter userEntityConverter;
    private final SuperGroupJpaRepository superGroupRepository;
    private final UserJpaRepository userRepository;
    private final PostJpaRepository postRepository;

    public AuthorityLevelEntityConverter(UserEntityConverter userEntityConverter,
                                         SuperGroupJpaRepository superGroupRepository,
                                         UserJpaRepository userRepository,
                                         PostJpaRepository postRepository) {
        this.userEntityConverter = userEntityConverter;
        this.superGroupRepository = superGroupRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public AuthorityLevel toDomain(AuthorityLevelEntity authorityLevelEntity) {
        return new AuthorityLevel(
                AuthorityLevelName.valueOf(authorityLevelEntity.getAuthorityLevel()),
                authorityLevelEntity.getPosts().stream().map(AuthorityPostEntity::getIdentifier).toList(),
                authorityLevelEntity.getSuperGroups().stream().map(AuthoritySuperGroupEntity::getIdentifier).toList(),
                authorityLevelEntity.getUsers().stream()
                        .map(AuthorityUserEntity::getUserEntity)
                        .map(this.userEntityConverter::toDomain)
                        .toList()
        );
    }

    public AuthorityLevelEntity toEntity(AuthorityLevel authorityLevel) {
        String name = authorityLevel.name().getValue();
        AuthorityLevelEntity authorityLevelEntity = new AuthorityLevelEntity(name);

        List<AuthorityUserEntity> users = authorityLevel.users().stream().map(user -> new AuthorityUserEntity(toEntity(user), authorityLevelEntity)).toList();
        List<AuthorityPostEntity> posts = authorityLevel.posts().stream().map(post -> new AuthorityPostEntity(toEntity(post.superGroup()), toEntity(post.post()), authorityLevelEntity)).toList();
        List<AuthoritySuperGroupEntity> superGroups = authorityLevel.superGroups().stream().map(superGroup -> new AuthoritySuperGroupEntity(toEntity(superGroup), authorityLevelEntity)).toList();

        authorityLevelEntity.setUsers(users);
        authorityLevelEntity.setPosts(posts);
        authorityLevelEntity.setSuperGroups(superGroups);

        return authorityLevelEntity;
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
