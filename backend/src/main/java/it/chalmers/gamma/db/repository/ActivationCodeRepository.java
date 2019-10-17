package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.ActivationCode;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCode, UUID> {
    ActivationCode findByCid_Cid(String cid);

    boolean existsActivationCodeByCid_Cid(String cid);
}
