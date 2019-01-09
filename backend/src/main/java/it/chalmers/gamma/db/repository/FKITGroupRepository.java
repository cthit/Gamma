package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.FKITGroup;

import java.util.List;
import java.util.UUID;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FKITGroupRepository extends JpaRepository<FKITGroup, UUID> {
    boolean existsFKITGroupByName(String name);
    List<FKITGroup> findAllBySuperGroup(FKITSuperGroup superGroup);
    FKITGroup findByName(String name);
}
