package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.user.domain.Name;
import org.springframework.stereotype.Service;

@Service
public class SuperGroupEntityConverter {

    public final SuperGroupJpaRepository superGroupJpaRepository;

    public SuperGroupEntityConverter(SuperGroupJpaRepository superGroupJpaRepository) {
        this.superGroupJpaRepository = superGroupJpaRepository;
    }

    public SuperGroup toDomain(SuperGroupEntity entity) {
        return new SuperGroup(
                new SuperGroupId(entity.id),
                entity.getVersion(),
                new Name(entity.name),
                new PrettyName(entity.prettyName),
                new SuperGroupType(entity.superGroupType.getId()),
                entity.description.toDomain()
        );
    }

}
