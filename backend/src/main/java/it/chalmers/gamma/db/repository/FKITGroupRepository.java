package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.FKITGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FKITGroupRepository extends JpaRepository<FKITGroup, UUID> {
    boolean existsFKITGroupByName(String name);
    FKITGroup findByName(String name);
}
