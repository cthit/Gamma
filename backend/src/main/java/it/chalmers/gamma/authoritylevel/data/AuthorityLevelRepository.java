package it.chalmers.gamma.authoritylevel.data;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityLevelRepository extends JpaRepository<AuthorityLevel, UUID> {
    Optional<AuthorityLevel> findByAuthorityLevel(String authorityLevel);

    boolean existsByAuthorityLevel(String authorityLevel);
    boolean existsById(UUID id);
}
