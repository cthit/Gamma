package it.chalmers.gamma.internal.group.service;

import java.util.List;
import java.util.Optional;

import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, GroupId> {
    List<Group> findAllBySuperGroupId(SuperGroupId id);
    Optional<Group> findByName(String name);
}
