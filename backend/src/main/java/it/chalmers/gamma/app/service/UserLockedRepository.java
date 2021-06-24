package it.chalmers.gamma.app.service;

import it.chalmers.gamma.app.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLockedRepository extends JpaRepository<UserLockedEntity, UserId> {

}
