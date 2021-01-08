package it.chalmers.gamma.activationcode;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCode, UUID> {
    Optional<ActivationCode> findByCid_Cid(String cid);
    boolean existsActivationCodeByCid_Cid(String cid);
}
