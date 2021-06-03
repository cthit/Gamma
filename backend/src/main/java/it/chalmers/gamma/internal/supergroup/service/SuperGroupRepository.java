package it.chalmers.gamma.internal.supergroup.service;

import java.util.Optional;

import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.domain.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuperGroupRepository extends JpaRepository<SuperGroupEntity, SuperGroupId> {
    Optional<SuperGroupEntity> findByEntityName(EntityName name);
}
