package it.chalmers.gamma.domain.activationcode.data.db;

import java.util.Optional;

import it.chalmers.gamma.domain.Cid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCode, Cid> {

    Optional<ActivationCode> findActivationCodeByCidAndCode(Cid cid, String code);

}
