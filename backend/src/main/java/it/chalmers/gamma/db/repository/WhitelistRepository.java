package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.Whitelist;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhitelistRepository extends JpaRepository<Whitelist, UUID> {
    Whitelist findByCid(String cid);
}
