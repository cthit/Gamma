package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.supergroup.SuperGroup;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupType;
import it.chalmers.gamma.app.domain.user.Name;
import org.springframework.stereotype.Service;

@Service
public class SuperGroupEntityConverter {

    public final SuperGroupJpaRepository superGroupJpaRepository;

    public SuperGroupEntityConverter(SuperGroupJpaRepository superGroupJpaRepository) {
        this.superGroupJpaRepository = superGroupJpaRepository;
    }

    public SuperGroupEntity toEntity(SuperGroup superGroup) {
        SuperGroupEntity superGroupEntity = this.superGroupJpaRepository.findById(superGroup.id().value())
                .orElse(new SuperGroupEntity());

        superGroupEntity.throwIfNotValidVersion(superGroup.version());

        superGroupEntity.id = superGroup.id().value();
        superGroupEntity.superGroupType = superGroup.type().value();
        superGroupEntity.email = superGroup.email().value();
        superGroupEntity.name = superGroup.name().value();
        superGroupEntity.prettyName = superGroup.prettyName().value();

        if (superGroupEntity.description == null) {
            superGroupEntity.description = new TextEntity();
        }

        superGroupEntity.description.apply(superGroup.description());

        return superGroupEntity;
    }

    public SuperGroup toDomain(SuperGroupEntity entity) {
        return new SuperGroup(
                new SuperGroupId(entity.id),
                entity.getVersion(),
                new Name(entity.name),
                new PrettyName(entity.prettyName),
                new SuperGroupType(entity.superGroupType),
                new Email(entity.email),
                entity.description.toDomain()
        );
    }

}
