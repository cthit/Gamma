package it.chalmers.gamma.domain.group.data.db;

import java.util.List;
import java.util.Optional;

import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, GroupId> {
    List<Group> findAllBySuperGroupId(SuperGroupId id);
    Optional<Group> findByName(String name);
}
