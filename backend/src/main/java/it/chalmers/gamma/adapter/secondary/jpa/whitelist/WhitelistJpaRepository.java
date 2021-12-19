package it.chalmers.gamma.adapter.secondary.jpa.whitelist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhitelistJpaRepository extends JpaRepository<WhitelistEntity, String> {
}
