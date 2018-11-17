package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Authority;
import it.chalmers.gamma.db.entity.AuthorityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface AuthorityLevelRepository extends JpaRepository<AuthorityLevel, UUID> {
    AuthorityLevel findByAuthorityLevel(String authorityLevel);
}
