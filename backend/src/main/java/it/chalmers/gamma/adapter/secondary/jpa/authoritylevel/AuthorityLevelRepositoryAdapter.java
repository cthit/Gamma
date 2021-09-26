package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.MembershipJpaRepository;
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
import it.chalmers.gamma.domain.authoritylevel.AuthorityType;
import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.supergroup.SuperGroup;
import it.chalmers.gamma.domain.user.User;
import it.chalmers.gamma.domain.user.UserAuthority;
import it.chalmers.gamma.domain.user.UserId;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class AuthorityLevelRepositoryAdapter implements AuthorityLevelRepository {

    private final AuthorityLevelJpaRepository repository;

    private final AuthorityPostJpaRepository authorityPostRepository;
    private final AuthoritySuperGroupJpaRepository authoritySuperGroupRepository;
    private final AuthorityUserJpaRepository authorityUserRepository;

    private final MembershipJpaRepository membershipJpaRepository;

    private final AuthorityLevelEntityConverter authorityLevelEntityConverter;

    public AuthorityLevelRepositoryAdapter(AuthorityLevelJpaRepository repository,
                                           AuthorityPostJpaRepository authorityPostRepository,
                                           AuthoritySuperGroupJpaRepository authoritySuperGroupRepository,
                                           AuthorityUserJpaRepository authorityUserRepository,
                                           SuperGroupJpaRepository superGroupRepository,
                                           UserJpaRepository userRepository,
                                           PostJpaRepository postRepository,
                                           MembershipJpaRepository membershipJpaRepository,
                                           AuthorityLevelEntityConverter authorityLevelEntityConverter) {
        this.repository = repository;
        this.authorityPostRepository = authorityPostRepository;
        this.authoritySuperGroupRepository = authoritySuperGroupRepository;
        this.authorityUserRepository = authorityUserRepository;
        this.membershipJpaRepository = membershipJpaRepository;
        this.authorityLevelEntityConverter = authorityLevelEntityConverter;
    }

    @Override
    public void create(AuthorityLevelName authorityLevelName) {
        if (!this.repository.existsById(authorityLevelName.value())) {
            repository.saveAndFlush(new AuthorityLevelEntity(authorityLevelName.getValue()));
        }
    }

    @Override
    public void delete(AuthorityLevelName authorityLevelName) {
        repository.deleteById(authorityLevelName.getValue());
    }

    @Override
    public void save(AuthorityLevel authorityLevel) {
        AuthorityLevelEntity entity = this.authorityLevelEntityConverter.toEntity(authorityLevel);
        entity.getUsers().stream().map(AuthorityUserEntity::getUserEntity).map(UserEntity::id).forEach(System.out::println);
        this.repository.saveAndFlush(entity);
    }

    @Override
    public List<AuthorityLevel> getAll() {
        return this.repository.findAll()
                .stream()
                .map(this.authorityLevelEntityConverter::toDomain)
                .toList();
    }

    @Override
    public List<UserAuthority> getByUser(UserId userId) {
        Set<UserAuthority> names = new HashSet<>();

        this.authorityUserRepository.findAllById_UserEntity_Id(userId.value())
                .forEach(authorityUserEntity -> names.add(
                        new UserAuthority(
                                authorityUserEntity.id().getValue().authorityLevelName(),
                                AuthorityType.AUTHORITY
                        )
                ));

        Set<SuperGroup> userSuperGroups = new HashSet<>();

        this.membershipJpaRepository.findAllById_User_Id(userId.value())
                .forEach(membershipEntity -> {
                    names.add(new UserAuthority(
                            new AuthorityLevelName(membershipEntity.id().getGroup().name),
                            AuthorityType.GROUP
                    ));
                    userSuperGroups.add(membershipEntity.id().getGroup().superGroup.toDomain());
                    this.authorityPostRepository.findAllById_SuperGroupEntity_Id_AndId_PostEntity_Id(
                            membershipEntity.id().getGroup().superGroup.toDomain().id().getValue(),
                            membershipEntity.id().getPost().toDomain().id().value()
                    ).forEach(authorityPostEntity -> names.add(new UserAuthority(
                            authorityPostEntity.id().getValue().authorityLevelName(),
                            AuthorityType.AUTHORITY
                    )));
                });

        userSuperGroups.forEach(superGroup -> names.add(new UserAuthority(
                new AuthorityLevelName(superGroup.name().value()),
                AuthorityType.SUPERGROUP
        )));

        userSuperGroups.forEach(superGroupId -> names.addAll(
                this.authoritySuperGroupRepository
                        .findAllById_SuperGroupEntity_Id(superGroupId.id().value())
                        .stream()
                        .map(AuthoritySuperGroupEntity::id)
                        .map(AuthoritySuperGroupPK::getValue)
                        .map(AuthoritySuperGroupPK.AuthoritySuperGroupPKDTO::authorityLevelName)
                        .map(authorityLevelName -> new UserAuthority(
                                authorityLevelName,
                                AuthorityType.AUTHORITY
                        ))
                        .toList()
                ));

        return new ArrayList<>(names);
    }

    @Override
    public Optional<AuthorityLevel> get(AuthorityLevelName authorityLevelName) {
        return this.repository.findById(authorityLevelName.getValue()).map(this.authorityLevelEntityConverter::toDomain);
    }

}
