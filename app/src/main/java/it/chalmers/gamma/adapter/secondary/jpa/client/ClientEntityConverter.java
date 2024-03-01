package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.apikey.ClientApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.client.restriction.ClientRestrictionEntity;
import it.chalmers.gamma.adapter.secondary.jpa.client.restriction.ClientRestrictionSuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.client.scope.ClientScopeEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.app.client.domain.*;
import it.chalmers.gamma.app.client.domain.restriction.ClientRestriction;
import it.chalmers.gamma.app.client.domain.restriction.ClientRestrictionId;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientEntityConverter {

  private final ApiKeyEntityConverter apiKeyEntityConverter;
  private final SuperGroupRepository superGroupRepository;
  private final ClientJpaRepository clientJpaRepository;
  private final SuperGroupJpaRepository superGroupJpaRepository;
  private final PasswordEncoder passwordEncoder;

  public ClientEntityConverter(
      ApiKeyEntityConverter apiKeyEntityConverter,
      SuperGroupRepository superGroupRepository,
      ClientJpaRepository clientJpaRepository,
      SuperGroupJpaRepository superGroupJpaRepository,
      PasswordEncoder passwordEncoder) {
    this.apiKeyEntityConverter = apiKeyEntityConverter;
    this.superGroupRepository = superGroupRepository;
    this.clientJpaRepository = clientJpaRepository;
    this.superGroupJpaRepository = superGroupJpaRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Client toDomain(ClientEntity clientEntity) {
    List<Scope> scopes = clientEntity.scopes.stream().map(ClientScopeEntity::getScope).toList();

    ClientOwner owner;
    if (clientEntity.official) {
      owner = new ClientOwnerOfficial();
    } else {
      owner = new ClientUserOwner(new UserId(clientEntity.createdBy));
    }

    return new Client(
        new ClientUid(clientEntity.getId()),
        new ClientId(clientEntity.clientId),
        new ClientSecret(clientEntity.clientSecret),
        new ClientRedirectUrl(clientEntity.webServerRedirectUrl),
        new PrettyName(clientEntity.prettyName),
        clientEntity.description.toDomain(),
        scopes,
        Optional.ofNullable(clientEntity.clientsApiKey)
            .map(ClientApiKeyEntity::getApiKeyEntity)
            .map(apiKeyEntityConverter::toDomain)
            .orElse(null),
        owner,
        clientEntity.clientRestriction == null
            ? null
            : new ClientRestriction(
                new ClientRestrictionId(clientEntity.clientRestriction.getRestrictionId()),
                clientEntity.clientRestriction.getSuperGroupRestrictions().stream()
                    .map(
                        clientRestrictionSuperGroupEntity ->
                            this.superGroupRepository
                                .get(
                                    clientRestrictionSuperGroupEntity
                                        .getId()
                                        .getValue()
                                        .superGroupId())
                                .orElseThrow())
                    .toList()));
  }

  public ClientEntity toEntity(Client client) {
    ClientEntity clientEntity =
        this.clientJpaRepository.findById(client.clientUid().value()).orElse(new ClientEntity());

    clientEntity.clientUid = client.clientUid().value();
    clientEntity.clientId = client.clientId().value();
    clientEntity.clientSecret = client.clientSecret().value();
    clientEntity.prettyName = client.prettyName().value();
    clientEntity.webServerRedirectUrl = client.clientRedirectUrl().value();

    if (clientEntity.description == null) {
      clientEntity.description = new TextEntity();
    }

    clientEntity.description.apply(client.description());

    switch (client.owner()) {
      case ClientOwnerOfficial():
        clientEntity.official = true;
        clientEntity.createdBy = null;
        break;
      case ClientUserOwner(UserId createdBy):
        clientEntity.official = false;
        clientEntity.createdBy = createdBy.value();
        break;
    }

    clientEntity.scopes.clear();
    clientEntity.scopes.addAll(
        client.scopes().stream().map(scope -> new ClientScopeEntity(clientEntity, scope)).toList());

    client
        .clientApiKey()
        .ifPresent(
            apiKey -> {
              ApiKeyEntity apiKeyEntity =
                  new ApiKeyEntity(
                      apiKey.id().value(),
                      apiKey.apiKeyToken().value(),
                      apiKey.prettyName().value(),
                      apiKey.keyType(),
                      new TextEntity(apiKey.description()));

              clientEntity.clientsApiKey = new ClientApiKeyEntity(clientEntity, apiKeyEntity);
            });

    client
        .restrictions()
        .ifPresent(
            clientRestriction -> {
              clientEntity.clientRestriction =
                  new ClientRestrictionEntity(
                      clientRestriction.id().value(), client.clientUid().value());

              List<ClientRestrictionSuperGroupEntity> clientRestrictionSuperGroupEntities =
                  clientRestriction.superGroups().stream()
                      .map(
                          superGroup ->
                              new ClientRestrictionSuperGroupEntity(
                                  clientEntity.clientRestriction,
                                  this.superGroupJpaRepository
                                      .findById(superGroup.id().value())
                                      .orElseThrow()))
                      .toList();

              clientEntity.clientRestriction.setSuperGroupRestrictions(
                  clientRestrictionSuperGroupEntities);
            });

    return clientEntity;
  }
}
