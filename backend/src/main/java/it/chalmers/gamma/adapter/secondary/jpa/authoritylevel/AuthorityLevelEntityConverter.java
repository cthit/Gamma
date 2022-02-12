package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthorityLevelEntityConverter {

    private final UserEntityConverter userEntityConverter;
    private final SuperGroupEntityConverter superGroupEntityConverter;
    private final PostEntityConverter postEntityConverter;

    public AuthorityLevelEntityConverter(UserEntityConverter userEntityConverter,
                                         SuperGroupEntityConverter superGroupEntityConverter,
                                         PostEntityConverter postEntityConverter) {
        this.userEntityConverter = userEntityConverter;
        this.superGroupEntityConverter = superGroupEntityConverter;
        this.postEntityConverter = postEntityConverter;
    }

    public AuthorityLevel toDomain(AuthorityLevelEntity authorityLevelEntity) {
        return new AuthorityLevel(
                AuthorityLevelName.valueOf(authorityLevelEntity.getAuthorityLevel()),
                authorityLevelEntity.getPosts()
                        .stream()
                        .map(authorityPostEntity -> new AuthorityLevel.SuperGroupPost(
                                this.superGroupEntityConverter.toDomain(authorityPostEntity.getSuperGroupEntity()),
                                this.postEntityConverter.toDomain(authorityPostEntity.getPost())
                        ))
                        .toList(),
                authorityLevelEntity.getSuperGroups()
                        .stream()
                        .map(AuthoritySuperGroupEntity::getSuperGroup)
                        .map(this.superGroupEntityConverter::toDomain)
                        .toList(),
                authorityLevelEntity.getUsers()
                        .stream()
                        .map(AuthorityUserEntity::getUserEntity)
                        .map(this.userEntityConverter::toDomain)
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

}
