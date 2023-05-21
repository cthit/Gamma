package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.app.authority.domain.Authority;
import it.chalmers.gamma.app.authority.domain.AuthorityName;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ClientAuthorityEntityConverter {

    private final ClientEntityConverter clientEntityConverter;
    private final UserEntityConverter userEntityConverter;
    private final SuperGroupEntityConverter superGroupEntityConverter;
    private final PostEntityConverter postEntityConverter;

    public ClientAuthorityEntityConverter(ClientEntityConverter clientEntityConverter,
                                          UserEntityConverter userEntityConverter,
                                          SuperGroupEntityConverter superGroupEntityConverter,
                                          PostEntityConverter postEntityConverter) {
        this.clientEntityConverter = clientEntityConverter;
        this.userEntityConverter = userEntityConverter;
        this.superGroupEntityConverter = superGroupEntityConverter;
        this.postEntityConverter = postEntityConverter;
    }

    public Authority toDomain(ClientAuthorityEntity clientAuthorityEntity) {
        Objects.requireNonNull(clientAuthorityEntity.getId());

        var id = clientAuthorityEntity.getId();

        return new Authority(
                this.clientEntityConverter.toDomain(id.client),
                AuthorityName.valueOf(id.name),
                clientAuthorityEntity.getPosts()
                        .stream()
                        .map(authorityPostEntity -> new Authority.SuperGroupPost(
                                this.superGroupEntityConverter.toDomain(authorityPostEntity.getId().postFK.getSuperGroupEntity()),
                                this.postEntityConverter.toDomain(authorityPostEntity.getId().postFK.getPostEntity())
                        ))
                        .toList(),
                clientAuthorityEntity.getSuperGroups()
                        .stream()
                        .map(ClientAuthoritySuperGroupEntity::getSuperGroup)
                        .map(this.superGroupEntityConverter::toDomain)
                        .toList(),
                clientAuthorityEntity.getUsers()
                        .stream()
                        .map(ClientAuthorityUserEntity::getUserEntity)
                        .map(this.userEntityConverter::toDomain)
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

}
