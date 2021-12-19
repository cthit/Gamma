package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorityLevelEntityConverter {

    private final UserEntityConverter userEntityConverter;
    private final SuperGroupEntityConverter superGroupEntityConverter;
    private final PostEntityConverter postEntityConverter;

    private final SuperGroupJpaRepository superGroupRepository;
    private final UserJpaRepository userRepository;
    private final PostJpaRepository postRepository;
    private final AuthorityLevelJpaRepository authorityLevelJpaRepository;

    public AuthorityLevelEntityConverter(UserEntityConverter userEntityConverter,
                                         SuperGroupEntityConverter superGroupEntityConverter,
                                         PostEntityConverter postEntityConverter,
                                         SuperGroupJpaRepository superGroupRepository,
                                         UserJpaRepository userRepository,
                                         PostJpaRepository postRepository,
                                         AuthorityLevelJpaRepository authorityLevelJpaRepository) {
        this.userEntityConverter = userEntityConverter;
        this.superGroupEntityConverter = superGroupEntityConverter;
        this.postEntityConverter = postEntityConverter;
        this.superGroupRepository = superGroupRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.authorityLevelJpaRepository = authorityLevelJpaRepository;
    }

    public AuthorityLevel toDomain(AuthorityLevelEntity authorityLevelEntity) {
        return new AuthorityLevel(
                AuthorityLevelName.valueOf(authorityLevelEntity.getAuthorityLevel()),
                authorityLevelEntity.getPosts()
                        .stream()
                        .map(authorityPostEntity -> new AuthorityLevel.SuperGroupPost(
                                this.superGroupEntityConverter.toDomain(authorityPostEntity.getSuperGroupEntity()),
                                this.postEntityConverter.toDomain(authorityPostEntity.getPost())
                        ))
                        .toList(),
                authorityLevelEntity.getSuperGroups()
                        .stream()
                        .map(AuthoritySuperGroupEntity::getSuperGroup)
                        .map(this.superGroupEntityConverter::toDomain)
                        .toList(),
                authorityLevelEntity.getUsers()
                        .stream()
                        .map(AuthorityUserEntity::getUserEntity)
                        .map(this.userEntityConverter::toDomain)
                        .toList()
        );
    }

    public AuthorityLevelEntity toEntity(AuthorityLevel authorityLevel) {
        String name = authorityLevel.name().getValue();
        AuthorityLevelEntity authorityLevelEntity = this.authorityLevelJpaRepository.findById(name)
                .orElse(new AuthorityLevelEntity(name));

        List<AuthorityUserEntity> users = authorityLevel.users().stream().map(user -> new AuthorityUserEntity(toEntity(user), authorityLevelEntity)).toList();
        List<AuthorityPostEntity> posts = authorityLevel.posts().stream().map(post -> new AuthorityPostEntity(toEntity(post.superGroup()), toEntity(post.post()), authorityLevelEntity)).toList();
        List<AuthoritySuperGroupEntity> superGroups = authorityLevel.superGroups().stream().map(superGroup -> new AuthoritySuperGroupEntity(toEntity(superGroup), authorityLevelEntity)).toList();

        authorityLevelEntity.postEntityList.clear();
        authorityLevelEntity.postEntityList.addAll(posts);

        authorityLevelEntity.userEntityList.clear();
        authorityLevelEntity.userEntityList.addAll(users);

        authorityLevelEntity.superGroupEntityList.clear();
        authorityLevelEntity.superGroupEntityList.addAll(superGroups);

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
