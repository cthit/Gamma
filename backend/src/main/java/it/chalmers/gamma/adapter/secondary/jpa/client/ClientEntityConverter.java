package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.app.client.domain.*;
import it.chalmers.gamma.app.client.domain.restriction.ClientRestriction;
import it.chalmers.gamma.app.client.domain.restriction.ClientRestrictionId;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientEntityConverter {

    private final ApiKeyEntityConverter apiKeyEntityConverter;
    private final SuperGroupRepository superGroupRepository;

    public ClientEntityConverter(ApiKeyEntityConverter apiKeyEntityConverter,
                                 SuperGroupRepository superGroupRepository) {
        this.apiKeyEntityConverter = apiKeyEntityConverter;
        this.superGroupRepository = superGroupRepository;
    }

    public Client toDomain(ClientEntity clientEntity) {
        List<Scope> scopes = clientEntity.scopes
                .stream()
                .map(ClientScopeEntity::getScope)
                .toList();

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
                new ClientOwnerOfficial(),
                clientEntity.clientRestriction == null ? null : new ClientRestriction(
                        new ClientRestrictionId(clientEntity.clientRestriction.getRestrictionId()),
                        clientEntity.clientRestriction.getSuperGroupRestrictions()
                                .stream()
                                .map(clientRestrictionSuperGroupEntity -> this.superGroupRepository.get(clientRestrictionSuperGroupEntity.getId().getValue().superGroupId()).orElseThrow())
                                .toList()
                )
        );
    }

}
