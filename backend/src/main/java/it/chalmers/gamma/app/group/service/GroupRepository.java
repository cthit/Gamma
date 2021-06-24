package it.chalmers.gamma.app.group.service;

import java.util.List;

import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, GroupId> {
    List<GroupEntity> findAllBySuperGroupId(SuperGroupId id);
}
