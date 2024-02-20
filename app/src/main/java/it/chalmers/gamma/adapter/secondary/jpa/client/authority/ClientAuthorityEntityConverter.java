package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.app.client.domain.authority.Authority;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ClientAuthorityEntityConverter {

  private final ClientEntityConverter clientEntityConverter;
  private final UserEntityConverter userEntityConverter;
  private final SuperGroupEntityConverter superGroupEntityConverter;

  public ClientAuthorityEntityConverter(
      ClientEntityConverter clientEntityConverter,
      UserEntityConverter userEntityConverter,
      SuperGroupEntityConverter superGroupEntityConverter) {
    this.clientEntityConverter = clientEntityConverter;
    this.userEntityConverter = userEntityConverter;
    this.superGroupEntityConverter = superGroupEntityConverter;
  }

  public Authority toDomain(ClientAuthorityEntity clientAuthorityEntity) {
    Objects.requireNonNull(clientAuthorityEntity.getId());

    var id = clientAuthorityEntity.getId();

    return new Authority(
        this.clientEntityConverter.toDomain(id.client),
        AuthorityName.valueOf(id.name),
        clientAuthorityEntity.getSuperGroups().stream()
            .map(ClientAuthoritySuperGroupEntity::getSuperGroup)
            .map(this.superGroupEntityConverter::toDomain)
            .toList(),
        clientAuthorityEntity.getUsers().stream()
            .map(ClientAuthorityUserEntity::getUserEntity)
            .map(this.userEntityConverter::toDomain)
            .filter(Objects::nonNull)
            .toList());
  }
}
