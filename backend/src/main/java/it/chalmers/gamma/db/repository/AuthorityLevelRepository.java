package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.AuthorityLevel;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityLevelRepository extends JpaRepository<AuthorityLevel, UUID> {
    AuthorityLevel findByAuthorityLevel(String authorityLevel);
}
