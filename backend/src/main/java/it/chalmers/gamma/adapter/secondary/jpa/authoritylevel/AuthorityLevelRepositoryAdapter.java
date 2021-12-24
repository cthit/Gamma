package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.MembershipJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.user.domain.UserAuthority;
import it.chalmers.gamma.app.user.domain.UserId;
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
    private final SuperGroupEntityConverter superGroupEntityConverter;

    public AuthorityLevelRepositoryAdapter(AuthorityLevelJpaRepository repository,
                                           AuthorityPostJpaRepository authorityPostRepository,
                                           AuthoritySuperGroupJpaRepository authoritySuperGroupRepository,
                                           AuthorityUserJpaRepository authorityUserRepository,
                                           MembershipJpaRepository membershipJpaRepository,
                                           AuthorityLevelEntityConverter authorityLevelEntityConverter,
                                           SuperGroupEntityConverter superGroupEntityConverter) {
        this.repository = repository;
        this.authorityPostRepository = authorityPostRepository;
        this.authoritySuperGroupRepository = authoritySuperGroupRepository;
        this.authorityUserRepository = authorityUserRepository;
        this.membershipJpaRepository = membershipJpaRepository;
        this.authorityLevelEntityConverter = authorityLevelEntityConverter;
        this.superGroupEntityConverter = superGroupEntityConverter;
    }

    @Override
    public void create(AuthorityLevelName authorityLevelName) {
        if (!this.repository.existsById(authorityLevelName.value())) {
            repository.save(new AuthorityLevelEntity(authorityLevelName.getValue()));
        }
    }

    @Override
    public void delete(AuthorityLevelName authorityLevelName) {
        repository.deleteById(authorityLevelName.getValue());
    }

    @Override
    public void save(AuthorityLevel authorityLevel) {
        AuthorityLevelEntity entity = this.authorityLevelEntityConverter.toEntity(authorityLevel);
        this.repository.save(entity);
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
                                authorityUserEntity.getId().getValue().authorityLevelName(),
                                AuthorityType.AUTHORITY
                        )
                ));

        Set<SuperGroup> userSuperGroups = new HashSet<>();

        this.membershipJpaRepository.findAllById_User_Id(userId.value())
                .forEach(membershipEntity -> {
                    names.add(new UserAuthority(
                            new AuthorityLevelName(membershipEntity.getId().getGroup().getName()),
                            AuthorityType.GROUP
                    ));

                    SuperGroupEntity superGroupEntity = membershipEntity.getId().getGroup().getSuperGroup();
                    userSuperGroups.add(this.superGroupEntityConverter.toDomain(superGroupEntity));

                    PostEntity postEntity = membershipEntity.getId().getPost();

                    this.authorityPostRepository.findAllById_SuperGroupEntity_Id_AndId_PostEntity_Id(
                            superGroupEntity.getId().getValue(),
                            postEntity.getId().value()
                    ).forEach(authorityPostEntity -> names.add(new UserAuthority(
                            authorityPostEntity.getId().getValue().authorityLevelName(),
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
                        .map(AuthoritySuperGroupEntity::getId)
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
