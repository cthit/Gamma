package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.authority.Authority;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import it.chalmers.gamma.app.client.domain.authority.ClientAuthorityRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.transaction.Transactional;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class ClientAuthorityRepositoryAdapter implements ClientAuthorityRepository {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ClientAuthorityRepositoryAdapter.class);
  private static final PersistenceErrorState notFoundError =
      new PersistenceErrorState(null, PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION);
  private final ClientAuthorityJpaRepository clientAuthorityJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final SuperGroupJpaRepository superGroupJpaRepository;
  private final ClientJpaRepository clientJpaRepository;
  private final ClientAuthorityEntityConverter clientAuthorityEntityConverter;

  public ClientAuthorityRepositoryAdapter(
      ClientAuthorityJpaRepository clientAuthorityJpaRepository,
      UserJpaRepository userJpaRepository,
      SuperGroupJpaRepository superGroupJpaRepository,
      ClientJpaRepository clientJpaRepository,
      ClientAuthorityEntityConverter clientAuthorityEntityConverter) {
    this.clientAuthorityJpaRepository = clientAuthorityJpaRepository;
    this.userJpaRepository = userJpaRepository;
    this.superGroupJpaRepository = superGroupJpaRepository;
    this.clientJpaRepository = clientJpaRepository;
    this.clientAuthorityEntityConverter = clientAuthorityEntityConverter;
  }

  @Override
  public void create(ClientUid clientUid, AuthorityName authorityName)
      throws ClientAuthorityAlreadyExistsException {
    if (clientAuthorityJpaRepository.existsById(toAuthorityEntityPK(clientUid, authorityName))) {
      throw new ClientAuthorityAlreadyExistsException(authorityName.value());
    }

    clientAuthorityJpaRepository.saveAndFlush(
        new ClientAuthorityEntity(
            this.clientJpaRepository.findById(clientUid.value()).orElseThrow(),
            authorityName.getValue()));
  }

  @Override
  public void delete(ClientUid clientUid, AuthorityName authorityName)
      throws ClientAuthorityNotFoundException {
    try {
      clientAuthorityJpaRepository.deleteById(toAuthorityEntityPK(clientUid, authorityName));
    } catch (EmptyResultDataAccessException e) {
      throw new ClientAuthorityNotFoundException();
    }
  }

  @Override
  public void save(Authority authority) throws ClientAuthorityNotFoundRuntimeException {
    ClientAuthorityEntity entity = toEntity(authority);

    try {
      this.clientAuthorityJpaRepository.saveAndFlush(entity);
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
    return this.clientAuthorityJpaRepository.findAllById_Client_Id(clientUid.value()).stream()
        .map(this.clientAuthorityEntityConverter::toDomain)
        .toList();
  }

  @Override
  public List<AuthorityName> getAllByUser(ClientUid clientUid, UserId userId) {
    return this.clientAuthorityJpaRepository
        .findAllClientAuthoritiesByUser(clientUid.value(), userId.value())
        .stream()
        .map(ClientAuthorityEntity::getId)
        .map(ClientAuthorityEntityPK::getValue)
        .map(ClientAuthorityEntityPK.AuthorityEntityPKRecord::authorityName)
        .toList();
  }

  @Override
  public Optional<Authority> get(ClientUid clientUid, AuthorityName authorityName) {
    return this.clientAuthorityJpaRepository
        .findById(toAuthorityEntityPK(clientUid, authorityName))
        .map(this.clientAuthorityEntityConverter::toDomain);
  }

  private ClientAuthorityEntity toEntity(Authority authority)
      throws ClientAuthorityNotFoundRuntimeException {
    ClientAuthorityEntity clientAuthorityEntity =
        this.clientAuthorityJpaRepository
            .findById(
                new ClientAuthorityEntityPK(
                    this.clientJpaRepository
                        .findById(authority.client().clientUid().value())
                        .orElseThrow(),
                    authority.name().value()))
            .orElseThrow(ClientAuthorityNotFoundRuntimeException::new);

    List<ClientAuthorityUserEntity> users =
        authority.users().stream()
            .map(user -> new ClientAuthorityUserEntity(toEntity(user), clientAuthorityEntity))
            .toList();
    List<ClientAuthoritySuperGroupEntity> superGroups =
        authority.superGroups().stream()
            .map(
                superGroup ->
                    new ClientAuthoritySuperGroupEntity(
                        toEntity(superGroup), clientAuthorityEntity))
            .toList();

    clientAuthorityEntity.userEntityList.clear();
    clientAuthorityEntity.userEntityList.addAll(users);

    clientAuthorityEntity.superGroupEntityList.clear();
    clientAuthorityEntity.superGroupEntityList.addAll(superGroups);

    return clientAuthorityEntity;
  }

  private UserEntity toEntity(GammaUser user) {
    return this.userJpaRepository.getReferenceById(user.id().getValue());
  }

  private SuperGroupEntity toEntity(SuperGroup superGroup) {
    return this.superGroupJpaRepository.getReferenceById(superGroup.id().getValue());
  }

  private ClientAuthorityEntityPK toAuthorityEntityPK(
      ClientUid clientUid, AuthorityName authorityName) {
    return new ClientAuthorityEntityPK(
        this.clientJpaRepository.findById(clientUid.value()).orElseThrow(), authorityName.value());
  }
}
