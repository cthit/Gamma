package it.chalmers.gamma.whitelist;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhitelistRepository extends JpaRepository<Whitelist, UUID> {
    Optional<Whitelist> findByCid(String cid);
    Boolean existsByCid(String cid);
}
