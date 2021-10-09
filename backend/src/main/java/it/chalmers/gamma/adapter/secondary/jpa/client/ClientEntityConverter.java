package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.client.ClientSecret;
import it.chalmers.gamma.app.domain.client.WebServerRedirectUrl;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.user.User;
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

        List<User> users = clientEntity.approvals
                .stream()
                .map(UserApprovalEntity::getUserEntity)
                .map(this.userEntityConverter::toDomain)
                .toList();

        return new Client(
                clientEntity.id(),
                new ClientSecret(clientEntity.clientSecret),
                new WebServerRedirectUrl(clientEntity.webServerRedirectUrl),
                clientEntity.autoApprove,
                new PrettyName(clientEntity.prettyName),
                clientEntity.description.toDomain(),
                restrictions,
                users,
                Optional.ofNullable(clientEntity.clientsApiKey)
                        .map(ClientApiKeyEntity::getApiKeyEntity)
                        .map(apiKeyEntityConverter::toDomain)
        );
    }

    protected ClientEntity toEntity(Client client) {
        ClientEntity clientEntity = this.clientJpaRepository
                .findById(client.clientId().value())
                .orElse(new ClientEntity());

        clientEntity.clientId = client.clientId().value();
        clientEntity.clientSecret = client.clientSecret().value();
        clientEntity.autoApprove = client.autoApprove();
        clientEntity.prettyName = client.prettyName().value();
        clientEntity.webServerRedirectUrl = client.webServerRedirectUrl().value();

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
                                this.userJpaRepository.getOne(user.id().value()),
                                clientEntity
                        ))
                        .toList()
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
