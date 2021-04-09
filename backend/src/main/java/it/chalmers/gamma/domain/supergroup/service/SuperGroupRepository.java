package it.chalmers.gamma.domain.supergroup.service;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuperGroupRepository extends JpaRepository<SuperGroup, SuperGroupId> {
    Optional<SuperGroup> findByName(String name);
    boolean existsByName(String name);
}
