package it.chalmers.gamma.internal.group.service;

import java.util.List;
import java.util.Optional;

import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.domain.Name;
import it.chalmers.gamma.domain.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, GroupId> {
    List<GroupEntity> findAllBySuperGroupId(SuperGroupId id);
    Optional<GroupEntity> findByName(Name name);
}
