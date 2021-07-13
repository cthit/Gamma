package it.chalmers.gamma.adapter.secondary.jpa.group;

import java.util.List;

import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupJpaRepository extends JpaRepository<GroupEntity, GroupId> {
    List<GroupEntity> findAllBySuperGroupId(SuperGroupId id);
}
