package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.app.domain.SuperGroupType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperGroupTypeRepository extends JpaRepository<SuperGroupTypeEntity, SuperGroupType> {
}
