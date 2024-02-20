package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.client.apikey.ClientApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.client.apikey.ClientApiKeyJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ClientRepositoryAdapter implements ClientRepository {

  private static final PersistenceErrorState authorityNotFound =
      new PersistenceErrorState(
          "g_authority_level_restriction_authority_fkey",
          PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION);
  private static final PersistenceErrorState userNotFound =
      new PersistenceErrorState(
          "g_user_approval_user_id_fkey", PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION);
  private static final PersistenceErrorState clientIdAlreadyExists =
      new PersistenceErrorState("g_client_id_key", PersistenceErrorState.Type.NOT_UNIQUE);
  private final ClientJpaRepository clientJpaRepository;
  private final ClientApiKeyJpaRepository clientApiKeyJpaRepository;
  private final ClientEntityConverter clientEntityConverter;
  private final UserApprovalJpaRepository userApprovalJpaRepository;
  private final UserJpaRepository userJpaRepository;

  public ClientRepositoryAdapter(
      ClientJpaRepository clientJpaRepository,
      ClientApiKeyJpaRepository clientApiKeyJpaRepository,
      ClientEntityConverter clientEntityConverter,
      UserApprovalJpaRepository userApprovalJpaRepository,
      UserJpaRepository userJpaRepository) {
    this.clientJpaRepository = clientJpaRepository;
    this.clientApiKeyJpaRepository = clientApiKeyJpaRepository;
    this.clientEntityConverter = clientEntityConverter;
    this.userApprovalJpaRepository = userApprovalJpaRepository;
    this.userJpaRepository = userJpaRepository;
  }

  @Override
  public void save(Client client) {
    try {
      this.clientJpaRepository.saveAndFlush(clientEntityConverter.toEntity(client));
    } catch (Exception e) {
      PersistenceErrorState state = PersistenceErrorHelper.getState(e);

      if (state.equals(authorityNotFound)) {
        throw new AuthorityNotFoundRuntimeException();
      } else if (state.equals(userNotFound)) {
        throw new UserNotFoundRuntimeException();
      } else if (state.equals(clientIdAlreadyExists)) {
        throw new ClientIdAlreadyExistsRuntimeException();
      }

      throw e;
    }
  }

  @Override
  public void delete(ClientUid clientUid) throws ClientNotFoundException {
    try {
      this.clientJpaRepository.deleteById(clientUid.value());
    } catch (EmptyResultDataAccessException e) {
      throw new ClientNotFoundException();
    }
  }

  @Override
  public List<Client> getAll() {
    return this.clientJpaRepository.findAll().stream()
        .map(clientEntityConverter::toDomain)
        .toList();
  }

  @Override
  public List<Client> getAllUserClients(UserId userId) {
    return this.clientJpaRepository.findAllByCreatedBy(userId.value()).stream()
        .map(clientEntityConverter::toDomain)
        .toList();
  }

  @Override
  public Optional<Client> get(ClientUid clientUid) {
    return this.clientJpaRepository
        .findById(clientUid.value())
        .map(this.clientEntityConverter::toDomain);
  }

  @Override
  public Optional<Client> get(ClientId clientId) {
    return this.clientJpaRepository
        .findByClientId(clientId.value())
        .map(this.clientEntityConverter::toDomain);
  }

  @Override
  public void addClientApproval(UserId userId, ClientUid clientUid) {
    UserApprovalEntity userApprovalEntity =
        new UserApprovalEntity(
            this.userJpaRepository.findById(userId.value()).orElseThrow(),
            this.clientJpaRepository.findById(clientUid.value()).orElseThrow());
    this.userApprovalJpaRepository.save(userApprovalEntity);
  }

  @Override
  public boolean isApprovedByUser(UserId userId, ClientUid clientUid) {
    return this.userApprovalJpaRepository.existsById_Client_ClientUidAndId_User_Id(
        clientUid.value(), userId.value());
  }

  @Override
  public List<Client> getClientsByUserApproved(UserId id) {
    return this.userApprovalJpaRepository.findAllById_User_Id(id.value()).stream()
        .map(UserApprovalEntity::getClientEntity)
        .map(this.clientEntityConverter::toDomain)
        .toList();
  }

  @Override
  public void deleteUserApproval(ClientUid clientUid, UserId userId) {
    this.userApprovalJpaRepository.deleteById_Client_ClientUidAndId_User_Id(
        clientUid.value(), userId.value());
  }

  @Override
  public Optional<Client> getByApiKey(ApiKeyToken apiKeyToken) {
    return this.clientApiKeyJpaRepository
        .findByApiKey_Token(apiKeyToken.value())
        .map(ClientApiKeyEntity::getClient)
        .map(this.clientEntityConverter::toDomain);
  }
}
