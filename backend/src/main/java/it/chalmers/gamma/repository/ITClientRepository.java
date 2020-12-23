package it.chalmers.gamma.repository;

import it.chalmers.gamma.db.entity.ITClient;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITClientRepository extends JpaRepository<ITClient, UUID> {

    Optional<ITClient> findByClientId(String clientId);

    boolean existsITClientByClientId(String clientId);
}
