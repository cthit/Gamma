package it.chalmers.gamma.group;

import it.chalmers.gamma.supergroup.SuperGroup;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    boolean existsFKITGroupByName(String name);
    Optional<Group> findByName(String name);
    void deleteByName(String name);
    List<Group> findAllBySuperGroupId(UUID id);
}
