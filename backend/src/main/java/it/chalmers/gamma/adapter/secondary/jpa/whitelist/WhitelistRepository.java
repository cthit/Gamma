package it.chalmers.gamma.adapter.secondary.jpa.whitelist;

import it.chalmers.gamma.app.domain.Cid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhitelistRepository extends JpaRepository<WhitelistEntity, Cid> {
}
