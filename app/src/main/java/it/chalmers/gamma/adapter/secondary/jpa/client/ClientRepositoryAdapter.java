package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.client.restriction.ClientRestrictionEntity;
import it.chalmers.gamma.adapter.secondary.jpa.client.restriction.ClientRestrictionSuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
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
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.transaction.Transactional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientRepositoryAdapter implements ClientRepository {

    private static final PersistenceErrorState authorityNotFound = new PersistenceErrorState(
            "itclient_authority_level_restriction_authority_fkey",
            PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION
    );
    private static final PersistenceErrorState userNotFound = new PersistenceErrorState(
            "it_user_approval_user_id_fkey",
            PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION
    );
    private static final PersistenceErrorState clientIdAlreadyExists = new PersistenceErrorState(
            "itclient_client_id_key",
            PersistenceErrorState.Type.NOT_UNIQUE
    );
    private final ClientJpaRepository clientJpaRepository;
    private final ClientApiKeyJpaRepository clientApiKeyJpaRepository;
    private final ClientEntityConverter clientEntityConverter;
    private final UserApprovalJpaRepository userApprovalJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final SuperGroupJpaRepository superGroupJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientRepositoryAdapter(ClientJpaRepository clientJpaRepository,
                                   ClientApiKeyJpaRepository clientApiKeyJpaRepository,
                                   ClientEntityConverter clientEntityConverter,
                                   UserApprovalJpaRepository userApprovalJpaRepository,
                                   UserJpaRepository userJpaRepository,
                                   SuperGroupJpaRepository superGroupJpaRepository,
                                   PasswordEncoder passwordEncoder) {
        this.clientJpaRepository = clientJpaRepository;
        this.clientApiKeyJpaRepository = clientApiKeyJpaRepository;
        this.clientEntityConverter = clientEntityConverter;
        this.userApprovalJpaRepository = userApprovalJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.superGroupJpaRepository = superGroupJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(Client client) {
        try {
            this.clientJpaRepository.saveAndFlush(toEntity(client));
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
        return this.clientJpaRepository.findAll()
                .stream()
                .map(clientEntityConverter::toDomain)
                .toList();
    }

    @Override
    public Optional<Client> get(ClientUid clientUid) {
        return this.clientJpaRepository.findById(clientUid.value())
                .map(this.clientEntityConverter::toDomain);
    }

    @Override
    public Optional<Client> get(ClientId clientId) {
        return this.clientJpaRepository.findByClientId(clientId.value())
                .map(this.clientEntityConverter::toDomain);
    }

    @Override
    public void addClientApproval(UserId userId, ClientUid clientUid) {
        UserApprovalEntity userApprovalEntity = new UserApprovalEntity(
                this.userJpaRepository.findById(userId.value()).orElseThrow(),
                this.clientJpaRepository.findById(clientUid.value()).orElseThrow()
        );
        this.userApprovalJpaRepository.save(userApprovalEntity);
    }

    @Override
    public boolean isApprovedByUser(UserId userId, ClientUid clientUid) {
        return this.userApprovalJpaRepository.existsById_Client_ClientUidAndId_User_Id(clientUid.value(), userId.value());
    }

    @Override
    public List<Client> getClientsByUserApproved(UserId id) {
        return this.userApprovalJpaRepository.findAllById_User_Id(id.value())
                .stream()
                .map(UserApprovalEntity::getClientEntity)
                .map(this.clientEntityConverter::toDomain)
                .toList();
    }

    @Override
    public void deleteUserApproval(ClientUid clientUid, UserId userId) {
        this.userApprovalJpaRepository.deleteById_Client_ClientUidAndId_User_Id(clientUid.value(), userId.value());
    }

    @Override
    public Optional<Client> getByApiKey(ApiKeyToken apiKeyToken) {
        return this.clientApiKeyJpaRepository
                .findByApiKey_Token(apiKeyToken.value())
                .map(ClientApiKeyEntity::getClient)
                .map(this.clientEntityConverter::toDomain);
    }

    private ClientEntity toEntity(Client client) {
        ClientEntity clientEntity = this.clientJpaRepository
                .findById(client.clientUid().value())
                .orElse(new ClientEntity());

        clientEntity.clientUid = client.clientUid().value();
        clientEntity.clientId = client.clientId().value();
        clientEntity.clientSecret = this.passwordEncoder.encode(client.clientSecret().value());
        clientEntity.prettyName = client.prettyName().value();
        clientEntity.webServerRedirectUrl = client.clientRedirectUrl().value();

        if (clientEntity.description == null) {
            clientEntity.description = new TextEntity();
        }

        clientEntity.description.apply(client.description());

        clientEntity.scopes.clear();
        clientEntity.scopes.addAll(
                client.scopes()
                        .stream()
                        .map(scope -> new ClientScopeEntity(
                                clientEntity,
                                scope)
                        ).toList()
        );

        client.clientApiKey().ifPresent(
                apiKey -> {
                    ApiKeyEntity apiKeyEntity = new ApiKeyEntity(
                            apiKey.id().value(),
                            apiKey.apiKeyToken().value(),
                            apiKey.prettyName().value(),
                            apiKey.keyType(),
                            new TextEntity(apiKey.description())
                    );

                    clientEntity.clientsApiKey = new ClientApiKeyEntity(clientEntity, apiKeyEntity);
                }
        );

        client.restrictions().ifPresent(
                clientRestriction -> {
                    clientEntity.clientRestriction = new ClientRestrictionEntity(clientRestriction.id().value(), client.clientUid().value());

                    List<ClientRestrictionSuperGroupEntity> clientRestrictionSuperGroupEntities = clientRestriction.superGroups()
                            .stream()
                            .map(superGroup -> new ClientRestrictionSuperGroupEntity(
                                            clientEntity.clientRestriction,
                                            this.superGroupJpaRepository.findById(superGroup.id().value()).orElseThrow()
                                    )
                            ).toList();

                    clientEntity.clientRestriction.setSuperGroupRestrictions(clientRestrictionSuperGroupEntities);
                }
        );

        return clientEntity;
    }

}
