package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITGroupToSuperGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface FKITGroupToSuperGroupRepository extends JpaRepository<FKITGroupToSuperGroup, UUID> {
    List<FKITGroupToSuperGroup> findFKITGroupToSuperGroupsById_SuperGroup(FKITSuperGroup superGroup);
    FKITGroupToSuperGroup findFKITGroupToSuperGroupsById_Group(FKITGroup group);
    FKITGroupToSuperGroup findFKITGroupToSuperGroupsById_GroupAndId_SuperGroup(FKITGroup group,
                                                                               FKITSuperGroup superGroup);
}
