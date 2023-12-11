package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.app.client.domain.*;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import it.chalmers.gamma.app.common.PrettyName;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientEntityConverter {

    private final UserEntityConverter userEntityConverter;
    private final ApiKeyEntityConverter apiKeyEntityConverter;

    public ClientEntityConverter(UserEntityConverter userEntityConverter,
                                 ApiKeyEntityConverter apiKeyEntityConverter) {
        this.userEntityConverter = userEntityConverter;
        this.apiKeyEntityConverter = apiKeyEntityConverter;
    }

    public Client toDomain(ClientEntity clientEntity) {
//        TODO:
//        List<AuthorityName> restrictions = clientEntity.restrictions
//                .stream()
//                .map(ClientRestrictionUserEntity::getAuthorityLevelName)
//                .toList();

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
                new ClientOwnerOfficial()
        );
    }

}
