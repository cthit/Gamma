package it.chalmers.gamma.domain.whitelist.data;

import it.chalmers.gamma.domain.Cid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhitelistRepository extends JpaRepository<Whitelist, Cid> { }
