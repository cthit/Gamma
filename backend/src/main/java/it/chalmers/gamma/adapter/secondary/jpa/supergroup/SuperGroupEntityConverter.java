package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.user.domain.Name;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SuperGroupEntityConverter {

    public final SuperGroupJpaRepository superGroupJpaRepository;

    public SuperGroupEntityConverter(SuperGroupJpaRepository superGroupJpaRepository) {
        this.superGroupJpaRepository = superGroupJpaRepository;
    }

    public SuperGroupEntity toEntity(SuperGroup superGroup) {
        Optional<SuperGroupEntity> maybeSuperGroupEntity = this.superGroupJpaRepository.findById(superGroup.id().value());
        SuperGroupEntity superGroupEntity;
        if (maybeSuperGroupEntity.isPresent()) {
            superGroupEntity = maybeSuperGroupEntity.get();
            superGroupEntity.increaseVersion(superGroup.version());
        } else {
            superGroupEntity = new SuperGroupEntity();
        }

        superGroupEntity.id = superGroup.id().value();
        superGroupEntity.superGroupType = superGroup.type().value();
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
                entity.description.toDomain()
        );
    }

}
