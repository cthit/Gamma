package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.FKITGroup;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FKITGroupRepository extends JpaRepository<FKITGroup, UUID> {
    boolean existsFKITGroupByName(String name);
    FKITGroup findByName(String name);
}
