package it.chalmers.gamma.domain.authoritylevel.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityLevelRepository extends JpaRepository<AuthorityLevel, String> {
}
