package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Whitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WhitelistRepository extends JpaRepository<Whitelist, UUID> {
    Whitelist findByCid(String cid);
}
