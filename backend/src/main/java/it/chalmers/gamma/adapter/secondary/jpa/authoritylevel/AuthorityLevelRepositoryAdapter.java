package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.MembershipJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserAuthority;
import it.chalmers.gamma.app.user.domain.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthorityLevelRepositoryAdapter implements AuthorityLevelRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityLevelRepositoryAdapter.class);

    private final AuthorityLevelJpaRepository repository;

    private final AuthorityPostJpaRepository authorityPostRepository;
    private final AuthoritySuperGroupJpaRepository authoritySuperGroupRepository;
    private final AuthorityUserJpaRepository authorityUserRepository;

    private final MembershipJpaRepository membershipJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final SuperGroupJpaRepository superGroupJpaRepository;
    private final PostJpaRepository postJpaRepository;

    private final AuthorityLevelEntityConverter authorityLevelEntityConverter;
    private final SuperGroupEntityConverter superGroupEntityConverter;

    private static final PersistenceErrorState superGroupNotFound = new PersistenceErrorState(
            "authority_super_group_super_group_id_fkey",
            PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION
    );

    private static final PersistenceErrorState postSuperGroupNotFound = new PersistenceErrorState(
            "authority_post_super_group_id_fkey",
            PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION
    );

    private static final PersistenceErrorState postPostNotFound = new PersistenceErrorState(
            "authority_post_post_id_fkey",
            PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION
    );

    private static final PersistenceErrorState userNotFound = new PersistenceErrorState(
            "authority_user_user_id_fkey",
            PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION
    );

    public AuthorityLevelRepositoryAdapter(AuthorityLevelJpaRepository repository,
                                           AuthorityPostJpaRepository authorityPostRepository,
                                           AuthoritySuperGroupJpaRepository authoritySuperGroupRepository,
                                           AuthorityUserJpaRepository authorityUserRepository,
                                           MembershipJpaRepository membershipJpaRepository,
                                           UserJpaRepository userJpaRepository,
                                           SuperGroupJpaRepository superGroupJpaRepository,
                                           PostJpaRepository postJpaRepository,
                                           AuthorityLevelEntityConverter authorityLevelEntityConverter,
                                           SuperGroupEntityConverter superGroupEntityConverter) {
        this.repository = repository;
        this.authorityPostRepository = authorityPostRepository;
        this.authoritySuperGroupRepository = authoritySuperGroupRepository;
        this.authorityUserRepository = authorityUserRepository;
        this.membershipJpaRepository = membershipJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.superGroupJpaRepository = superGroupJpaRepository;
        this.postJpaRepository = postJpaRepository;
        this.authorityLevelEntityConverter = authorityLevelEntityConverter;
        this.superGroupEntityConverter = superGroupEntityConverter;
    }

    @Override
    public void create(AuthorityLevelName authorityLevelName) throws AuthorityLevelAlreadyExistsException {
        try {
            repository.saveAndFlush(new AuthorityLevelEntity(authorityLevelName.getValue()));
        } catch (Exception e) {
            if (e.getCause() instanceof EntityExistsException) {
                throw new AuthorityLevelRepository.AuthorityLevelAlreadyExistsException();
            }
            throw e;
        }
    }

    @Override
    public void delete(AuthorityLevelName authorityLevelName) throws AuthorityLevelNotFoundException {
        try {
            repository.deleteById(authorityLevelName.getValue());
        } catch (EmptyResultDataAccessException e) {
            throw new AuthorityLevelNotFoundException();
        }
    }

    @Override
    public void save(AuthorityLevel authorityLevel)
            throws AuthorityLevelNotFoundRuntimeException {
        AuthorityLevelEntity entity = toEntity(authorityLevel);

        try {
            this.repository.saveAndFlush(entity);
        } catch (Exception e) {
            PersistenceErrorState state = PersistenceErrorHelper.getState(e);

            if (state.equals(superGroupNotFound)) {
                throw new SuperGroupNotFoundRuntimeException();
            } else if (state.equals(userNotFound)) {
                throw new UserNotFoundRuntimeException();
            } else if (state.equals(postSuperGroupNotFound)
                    || state.equals(postPostNotFound)) {
                throw new SuperGroupPostNotFoundRuntimeException();
            }

            throw e;
        }
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
                            superGroupEntity.getId(),
                            postEntity.getId()
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

    private AuthorityLevelEntity toEntity(AuthorityLevel authorityLevel) throws AuthorityLevelRepository.AuthorityLevelNotFoundRuntimeException {
        String name = authorityLevel.name().getValue();
        AuthorityLevelEntity authorityLevelEntity = this.repository.findById(name)
                .orElseThrow(AuthorityLevelRepository.AuthorityLevelNotFoundRuntimeException::new);

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
        return this.userJpaRepository.getById(user.id().getValue());
    }

    private PostEntity toEntity(Post post) {
        return this.postJpaRepository.getById(post.id().getValue());
    }

    private SuperGroupEntity toEntity(SuperGroup superGroup) {
        return this.superGroupJpaRepository.getById(superGroup.id().getValue());
    }

}
