package it.chalmers.gamma.app.useractivation.service;

import java.util.Optional;

import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.UserActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivationRepository extends JpaRepository<UserActivationEntity, Cid> {

    Optional<UserActivationEntity> findUserActivationByCidAndToken(Cid cid, UserActivationToken token);

}
