package it.chalmers.gamma.authority;

import it.chalmers.gamma.authority.AuthorityLevel;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityLevelRepository extends JpaRepository<AuthorityLevel, UUID> {
    Optional<AuthorityLevel> findByAuthorityLevel(String authorityLevel);
    Boolean existsByAuthorityLevel(String authorityLevel);
}