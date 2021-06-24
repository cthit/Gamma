package it.chalmers.gamma.app.authoritylevel.service;

import it.chalmers.gamma.app.domain.AuthorityLevelName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityLevelRepository extends JpaRepository<AuthorityLevelEntity, AuthorityLevelName> { }
