package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FKITSuperGroupRepository extends JpaRepository<FKITSuperGroup, UUID> {
    FKITSuperGroup getById(UUID id);
    FKITSuperGroup getByName(String name);
    boolean existsByName(String name);
}
