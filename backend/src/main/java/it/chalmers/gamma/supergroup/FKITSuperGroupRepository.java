package it.chalmers.gamma.supergroup;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FKITSuperGroupRepository extends JpaRepository<FKITSuperGroup, UUID> {
    Optional<FKITSuperGroup> findByName(String name);
    boolean existsByName(String name);
}
