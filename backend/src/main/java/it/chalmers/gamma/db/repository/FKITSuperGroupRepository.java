package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.FKITSuperGroup;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FKITSuperGroupRepository extends JpaRepository<FKITSuperGroup, UUID> {
    FKITSuperGroup getById(UUID id);
    boolean existsByName(String name);
}
