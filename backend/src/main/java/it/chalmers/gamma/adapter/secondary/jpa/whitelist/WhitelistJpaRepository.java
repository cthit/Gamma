package it.chalmers.gamma.adapter.secondary.jpa.whitelist;

import it.chalmers.gamma.app.domain.user.Cid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhitelistJpaRepository extends JpaRepository<WhitelistEntity, String> {
}
