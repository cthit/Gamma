package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientJpaRepository;
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
import it.chalmers.gamma.app.client.domain.authority.Authority;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import it.chalmers.gamma.app.client.domain.authority.ClientAuthorityRepository;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;

@Transactional
@Service
public class ClientAuthorityRepositoryAdapter implements ClientAuthorityRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAuthorityRepositoryAdapter.class);
    private static final PersistenceErrorState notFoundError = new PersistenceErrorState(null, PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION);
    private final ClientAuthorityJpaRepository repository;
    private final ClientAuthorityPostJpaRepository authorityPostRepository;
    private final ClientAuthoritySuperGroupJpaRepository authoritySuperGroupRepository;
    private final ClientAuthorityUserJpaRepository authorityUserRepository;
    private final MembershipJpaRepository membershipJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final SuperGroupJpaRepository superGroupJpaRepository;
    private final ClientJpaRepository clientJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final ClientAuthorityEntityConverter clientAuthorityEntityConverter;
    private final SuperGroupEntityConverter superGroupEntityConverter;

    public ClientAuthorityRepositoryAdapter(ClientAuthorityJpaRepository repository, ClientAuthorityPostJpaRepository authorityPostRepository, ClientAuthoritySuperGroupJpaRepository authoritySuperGroupRepository, ClientAuthorityUserJpaRepository authorityUserRepository, MembershipJpaRepository membershipJpaRepository, UserJpaRepository userJpaRepository, SuperGroupJpaRepository superGroupJpaRepository, ClientJpaRepository clientJpaRepository, PostJpaRepository postJpaRepository, ClientAuthorityEntityConverter clientAuthorityEntityConverter, SuperGroupEntityConverter superGroupEntityConverter) {
        this.repository = repository;
        this.authorityPostRepository = authorityPostRepository;
        this.authoritySuperGroupRepository = authoritySuperGroupRepository;
        this.authorityUserRepository = authorityUserRepository;
        this.membershipJpaRepository = membershipJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.superGroupJpaRepository = superGroupJpaRepository;
        this.clientJpaRepository = clientJpaRepository;
        this.postJpaRepository = postJpaRepository;
        this.clientAuthorityEntityConverter = clientAuthorityEntityConverter;
        this.superGroupEntityConverter = superGroupEntityConverter;
    }

    @Override
    public void create(ClientUid clientUid, AuthorityName authorityName) throws ClientAuthorityAlreadyExistsException {
        if (repository.existsById(toAuthorityEntityPK(clientUid, authorityName))) {
            throw new ClientAuthorityAlreadyExistsException(authorityName.value());
        }

        repository.saveAndFlush(new ClientAuthorityEntity(this.clientJpaRepository.getReferenceById(clientUid.value()), authorityName.getValue()));
    }

    @Override
    public void delete(ClientUid clientUid, AuthorityName authorityName) throws ClientAuthorityNotFoundException {
        try {
            repository.deleteById(toAuthorityEntityPK(clientUid, authorityName));
        } catch (EmptyResultDataAccessException e) {
            throw new ClientAuthorityNotFoundException();
        }
    }

    @Override
    public void save(Authority authority) throws ClientAuthorityNotFoundRuntimeException {
        ClientAuthorityEntity entity = toEntity(authority);

        try {
            this.repository.saveAndFlush(entity);
        } catch (Exception e) {
            PersistenceErrorState state = PersistenceErrorHelper.getState(e);

            if (state.equals(notFoundError)) {
                throw new NotCompleteClientAuthorityException();
            }

            throw e;
        }
    }

    @Override
    public List<Authority> getAllByClient(ClientUid clientUid) {
        return this.repository.findAllById_Client_Id(clientUid.value()).stream().map(this.clientAuthorityEntityConverter::toDomain).toList();
    }

    @Override
    public List<Authority> getAllByUser(ClientUid clientUid, UserId userId) {

            throw new UnsupportedOperationException();
//        Set<UserAuthority> names = new HashSet<>();
//
//        this.authorityUserRepository.findAllById_UserEntity_Id(userId.value()).forEach(authorityUserEntity -> names.add(new UserAuthority(authorityUserEntity.getId().getValue().authorityName(), AuthorityType.AUTHORITY)));
//
//        Set<SuperGroup> userSuperGroups = new HashSet<>();
//
//        this.membershipJpaRepository.findAllById_User_Id(userId.value()).forEach(membershipEntity -> {
//            names.add(new UserAuthority(new AuthorityName(membershipEntity.getId().getGroup().getName()), AuthorityType.GROUP));
//
//            SuperGroupEntity superGroupEntity = membershipEntity.getId().getGroup().getSuperGroup();
//            userSuperGroups.add(this.superGroupEntityConverter.toDomain(superGroupEntity));
//
//            PostEntity postEntity = membershipEntity.getId().getPost();
//
//            this.authorityPostRepository.findAllById_SuperGroupEntity_Id_AndId_PostEntity_Id(superGroupEntity.getId(), postEntity.getId()).forEach(authorityPostEntity -> names.add(new UserAuthority(authorityPostEntity.getId().getValue().authorityName(), AuthorityType.AUTHORITY)));
//        });
//
//        userSuperGroups.forEach(superGroup -> names.add(new UserAuthority(new AuthorityName(superGroup.name().value()), AuthorityType.SUPERGROUP)));
//
//        userSuperGroups.forEach(superGroupId -> names.addAll(this.authoritySuperGroupRepository.findAllById_SuperGroupEntity_Id(superGroupId.id().value()).stream().map(AuthoritySuperGroupEntity::getId).map(AuthoritySuperGroupPK::getValue).map(AuthoritySuperGroupPK.AuthoritySuperGroupPKDTO::authorityName).map(clientAuthority -> new UserAuthority(clientAuthority, AuthorityType.AUTHORITY)).toList()));
//
//        return new ArrayList<>(names);
    }

    @Override
    public Optional<Authority> get(ClientUid clientUid, AuthorityName authorityName) {
        return this.repository.findById(toAuthorityEntityPK(clientUid, authorityName)).map(this.clientAuthorityEntityConverter::toDomain);
    }

    private ClientAuthorityEntity toEntity(Authority authority) throws ClientAuthorityNotFoundRuntimeException {
        String name = authority.name().getValue();

        ClientAuthorityEntity clientAuthorityEntity = this.repository.findById(new ClientAuthorityEntityPK()).orElseThrow(ClientAuthorityNotFoundRuntimeException::new);

        List<ClientAuthorityUserEntity> users = authority.users().stream().map(user -> new ClientAuthorityUserEntity(toEntity(user), clientAuthorityEntity)).toList();
        List<ClientAuthorityPostEntity> posts = authority.posts().stream().map(post -> new ClientAuthorityPostEntity(toEntity(post.superGroup()), toEntity(post.post()), clientAuthorityEntity)).toList();
        List<ClientAuthoritySuperGroupEntity> superGroups = authority.superGroups().stream().map(superGroup -> new ClientAuthoritySuperGroupEntity(toEntity(superGroup), clientAuthorityEntity)).toList();

        clientAuthorityEntity.postEntityList.clear();
        clientAuthorityEntity.postEntityList.addAll(posts);

        clientAuthorityEntity.userEntityList.clear();
        clientAuthorityEntity.userEntityList.addAll(users);

        clientAuthorityEntity.superGroupEntityList.clear();
        clientAuthorityEntity.superGroupEntityList.addAll(superGroups);

        return clientAuthorityEntity;
    }


    private UserEntity toEntity(GammaUser user) {
        return this.userJpaRepository.getById(user.id().getValue());
    }

    private PostEntity toEntity(Post post) {
        return this.postJpaRepository.getById(post.id().getValue());
    }

    private SuperGroupEntity toEntity(SuperGroup superGroup) {
        return this.superGroupJpaRepository.getById(superGroup.id().getValue());
    }

    private ClientAuthorityEntityPK toAuthorityEntityPK(ClientUid clientUid, AuthorityName authorityName) {
        return new ClientAuthorityEntityPK(this.clientJpaRepository.getReferenceById(clientUid.value()), authorityName.value());
    }

}
