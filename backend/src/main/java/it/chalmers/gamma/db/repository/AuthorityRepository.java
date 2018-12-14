package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Authority findById_FkitSuperGroupAndId_Post(FKITSuperGroup superGroup, Post post);

    List<Authority> findAllByAuthorityLevel(AuthorityLevel authorityLevel);

    Authority findByInternalId(UUID id);

    void deleteByInternalId(UUID id);
}
