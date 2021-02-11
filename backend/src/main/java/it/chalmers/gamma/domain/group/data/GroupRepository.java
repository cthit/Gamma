package it.chalmers.gamma.domain.group.data;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    boolean existsByName(String name);
    List<Group> findAllBySuperGroupId(UUID id);

    Optional<Group> findByName(String name);
}
