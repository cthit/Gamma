package it.chalmers.gamma.internal.authoritylevel.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityLevelRepository extends JpaRepository<AuthorityLevelEntity, String> {
}
