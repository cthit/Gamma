package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.app.client.domain.authority.Authority;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import it.chalmers.gamma.app.client.domain.authority.AuthoritySuperGroupPost;
import java.util.Comparator;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ClientAuthorityEntityConverter {

  private final ClientEntityConverter clientEntityConverter;
  private final UserEntityConverter userEntityConverter;
  private final SuperGroupEntityConverter superGroupEntityConverter;
  private final PostEntityConverter postEntityConverter;

  public ClientAuthorityEntityConverter(
      ClientEntityConverter clientEntityConverter,
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
        clientAuthorityEntity.getSuperGroups().stream()
            .map(ClientAuthoritySuperGroupEntity::getSuperGroup)
            .map(this.superGroupEntityConverter::toDomain)
            .toList(),
        clientAuthorityEntity.getSuperGroupPosts().stream()
            .map(
                superGroupPost ->
                    new AuthoritySuperGroupPost(
                        this.superGroupEntityConverter.toDomain(superGroupPost.getSuperGroup()),
                        this.postEntityConverter.toDomain(superGroupPost.getPost())))
            .sorted(
                Comparator.comparing(
                        (AuthoritySuperGroupPost superGroupPost) ->
                            superGroupPost.superGroup().prettyName().value())
                    .thenComparing(superGroupPost -> superGroupPost.post().order().value()))
            .toList(),
        clientAuthorityEntity.getUsers().stream()
            .map(ClientAuthorityUserEntity::getUserEntity)
            .map(this.userEntityConverter::toDomain)
            .filter(Objects::nonNull)
            .toList());
  }
}
