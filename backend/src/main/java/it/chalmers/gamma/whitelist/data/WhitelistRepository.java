package it.chalmers.gamma.whitelist.data;

import java.util.Optional;
import java.util.UUID;

import it.chalmers.gamma.whitelist.data.Whitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhitelistRepository extends JpaRepository<Whitelist, String> { }
