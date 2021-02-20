package it.chalmers.gamma.domain.group.data;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, GroupId> {
    boolean existsByName(String name);
    List<Group> findAllBySuperGroupId(SuperGroupId id);

    Optional<Group> findByName(String name);
}
