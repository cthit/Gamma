package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FKITSuperGroupRepository extends JpaRepository<FKITSuperGroup, UUID> {
    FKITSuperGroup getById(UUID id);
    boolean existsByName(String name);
}
