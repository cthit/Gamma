package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientSecret;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.Scope;
import it.chalmers.gamma.app.client.domain.RedirectUrl;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.user.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientEntityConverter {

    private final ClientJpaRepository clientJpaRepository;
    private final AuthorityLevelJpaRepository authorityLevelJpaRepository;
    private final UserJpaRepository userJpaRepository;

    private final UserEntityConverter userEntityConverter;
    private final ApiKeyEntityConverter apiKeyEntityConverter;

    public ClientEntityConverter(ClientJpaRepository clientJpaRepository,
                                 UserEntityConverter userEntityConverter,
                                 AuthorityLevelJpaRepository authorityLevelJpaRepository,
                                 UserJpaRepository userJpaRepository,
                                 ApiKeyEntityConverter apiKeyEntityConverter) {
        this.clientJpaRepository = clientJpaRepository;
        this.userEntityConverter = userEntityConverter;
        this.authorityLevelJpaRepository = authorityLevelJpaRepository;
        this.userJpaRepository = userJpaRepository;
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

        List<User> users = clientEntity.approvals
                .stream()
                .map(UserApprovalEntity::getUserEntity)
                .map(this.userEntityConverter::toDomain)
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

    protected ClientEntity toEntity(Client client) {
        ClientEntity clientEntity = this.clientJpaRepository
                .findById(client.clientUid().value())
                .orElse(new ClientEntity());

        clientEntity.clientUid = client.clientUid().value();
        clientEntity.clientId = client.clientId().value();
        clientEntity.clientSecret = client.clientSecret().value();
        clientEntity.prettyName = client.prettyName().value();
        clientEntity.webServerRedirectUrl = client.redirectUrl().value();

        if (clientEntity.description == null) {
            clientEntity.description = new TextEntity();
        }

        clientEntity.description.apply(client.description());

        clientEntity.restrictions.clear();
        clientEntity.restrictions.addAll(
                client.restrictions()
                        .stream()
                        .map(authorityLevelName -> new ClientRestrictionEntity(
                                clientEntity,
                                this.authorityLevelJpaRepository.getOne(
                                        authorityLevelName.value()
                                )
                        ))
                        .toList()
        );

        clientEntity.approvals.clear();
        clientEntity.approvals.addAll(
                client.approvedUsers()
                        .stream()
                        .map(user -> new UserApprovalEntity(
                                //TODO: Remove all getOne
                                this.userJpaRepository.getOne(user.id().value()),
                                clientEntity
                        ))
                        .toList()
        );

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
                apiKey -> clientEntity.clientsApiKey = new ClientApiKeyEntity(
                        clientEntity,
                        this.apiKeyEntityConverter.toEntity(apiKey)
                )
        );

        return clientEntity;
    }

}
