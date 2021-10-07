package it.chalmers.gamma.adapter.secondary.jpa.client;

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
import it.chalmers.gamma.app.domain.common.Text;
import it.chalmers.gamma.app.domain.user.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ClientEntityConverter {

    private final ClientJpaRepository clientJpaRepository;
    private final UserEntityConverter userEntityConverter;
    private final AuthorityLevelJpaRepository authorityLevelJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public ClientEntityConverter(ClientJpaRepository clientJpaRepository,
                                 UserEntityConverter userEntityConverter,
                                 AuthorityLevelJpaRepository authorityLevelJpaRepository,
                                 UserJpaRepository userJpaRepository) {
        this.clientJpaRepository = clientJpaRepository;
        this.userEntityConverter = userEntityConverter;
        this.authorityLevelJpaRepository = authorityLevelJpaRepository;
        this.userJpaRepository = userJpaRepository;
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
                new WebServerRedirectUrl(clientEntity.webServerRedirectUri),
                clientEntity.autoApprove,
                new PrettyName(clientEntity.prettyName),
                clientEntity.description.toDomain(),
                restrictions,
                users,
                Optional.ofNullable(clientEntity.clientsApiKey)
                        .map(ClientApiKeyEntity::getApiKey)
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
        clientEntity.webServerRedirectUri = client.webServerRedirectUrl().value();

        if (clientEntity.description == null) {
            clientEntity.description = new TextEntity();
        }

        clientEntity.description.apply(client.description());

        clientEntity.restrictions = client.restrictions()
                .stream()
                .map(authorityLevelName -> new ClientRestrictionEntity(
                        clientEntity,
                        this.authorityLevelJpaRepository.getOne(
                                authorityLevelName.value()
                        )
                ))
                .toList();

        clientEntity.approvals = client.approvedUsers()
                .stream()
                .map(user -> new UserApprovalEntity(
                        this.userJpaRepository.getOne(user.id().value()),
                        clientEntity
                ))
                .toList();

        return clientEntity;
    }

}
