package it.chalmers.gamma.activationcode;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCode, String> {

    Optional<ActivationCode> findActivationCodeByCidAndCode(String cid, String code);

}
