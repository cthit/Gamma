package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITGroupToSuperGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.domain.dto.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.FKITSuperGroupDTO;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FKITGroupToSuperGroupRepository extends JpaRepository<FKITGroupToSuperGroup, UUID> {
    List<FKITGroupToSuperGroup> findFKITGroupToSuperGroupsById_SuperGroup(FKITSuperGroupDTO superGroup);
    List<FKITGroupToSuperGroup> findAllFKITGroupToSuperGroupsById_Group(FKITGroupDTO group);
    FKITGroupToSuperGroup findFKITGroupToSuperGroupsById_GroupAndId_SuperGroup(FKITGroupDTO group,
                                                                               FKITSuperGroupDTO superGroup);
}
