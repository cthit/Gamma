package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCode, UUID>{
    ActivationCode findByCid(Whitelist cid);
}
