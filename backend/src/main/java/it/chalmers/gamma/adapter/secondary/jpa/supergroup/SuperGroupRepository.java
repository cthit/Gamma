package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import java.util.List;

import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuperGroupRepository extends JpaRepository<SuperGroupEntity, SuperGroupId> {
    List<SuperGroupEntity> findAllBySuperGroupType(SuperGroupType superGroupType);
}

