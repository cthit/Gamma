package it.chalmers.gamma.internal.authority.service.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityUserRepository extends JpaRepository<AuthorityUser, AuthorityUserPK> {
}
