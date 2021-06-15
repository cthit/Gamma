package it.chalmers.gamma.internal.activationcode.service;

import java.util.Optional;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.ActivationCodeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCodeEntity, Cid> {

    Optional<ActivationCodeEntity> findActivationCodeByCidAndCode(Cid cid, ActivationCodeToken token);

}
