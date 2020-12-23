package it.chalmers.gamma.repository;

import it.chalmers.gamma.db.entity.FKITGroup;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FKITGroupRepository extends JpaRepository<FKITGroup, UUID> {
    boolean existsFKITGroupByName(String name);
    Optional<FKITGroup> findByName(String name);
    void deleteByName(String name);
    List<FKITGroup> findAllBySuperGroup(FKITSuperGroup group);
}
