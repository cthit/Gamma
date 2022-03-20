package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientSecret;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.RedirectUrl;
import it.chalmers.gamma.app.client.domain.Scope;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.user.domain.GammaUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
        List<AuthorityLevelName> restrictions = clientEntity.restrictions
                .stream()
                .map(ClientRestrictionEntity::getAuthorityLevelName)
                .toList();

        List<Scope> scopes = clientEntity.scopes
                .stream()
                .map(ClientScopeEntity::getScope)
                .toList();

        List<GammaUser> users = clientEntity.approvals
                .stream()
                .map(UserApprovalEntity::getUserEntity)
                .map(this.userEntityConverter::toDomain)
                .filter(Objects::nonNull)
                .toList();

        return new Client(
                new ClientUid(clientEntity.getId()),
                new ClientId(clientEntity.clientId),
                new ClientSecret(clientEntity.clientSecret),
                new RedirectUrl(clientEntity.webServerRedirectUrl),
                new PrettyName(clientEntity.prettyName),
                clientEntity.description.toDomain(),
                restrictions,
                scopes,
                users,
                Optional.ofNullable(clientEntity.clientsApiKey)
                        .map(ClientApiKeyEntity::getApiKeyEntity)
                        .map(apiKeyEntityConverter::toDomain)
        );
    }

}
