package it.chalmers.gamma.internal.whitelist.service;

import it.chalmers.gamma.util.domain.Cid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhitelistRepository extends JpaRepository<Whitelist, Cid> {
}