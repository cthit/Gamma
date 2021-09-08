package it.chalmers.gamma.adapter.secondary.jpa.group;

import java.util.List;

import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupJpaRepository extends JpaRepository<GroupEntity, GroupId> {
    List<GroupEntity> findAllBySuperGroupId(SuperGroupId id);
}
