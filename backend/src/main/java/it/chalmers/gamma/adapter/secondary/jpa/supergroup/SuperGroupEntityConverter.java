package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.app.domain.supergroup.SuperGroup;
import org.springframework.stereotype.Service;

@Service
public class SuperGroupEntityConverter {

    public final SuperGroupJpaRepository superGroupJpaRepository;

    public SuperGroupEntityConverter(SuperGroupJpaRepository superGroupJpaRepository) {
        this.superGroupJpaRepository = superGroupJpaRepository;
    }

    public SuperGroupEntity toEntity(SuperGroup superGroup) {
        throw new UnsupportedOperationException();
    }

    public SuperGroup toDomain(SuperGroupEntity entity) {
        throw new UnsupportedOperationException();
    }

}
