package it.chalmers.gamma.internal.userlocked.service;

import it.chalmers.gamma.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLockedRepository extends JpaRepository<UserLockedEntity, UserId> {

}
