package it.chalmers.gamma.internal.authority.user.service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityUserRepository extends JpaRepository<AuthorityUser, AuthorityUserPK> {
}
