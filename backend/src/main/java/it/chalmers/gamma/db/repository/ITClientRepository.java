package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.ITClient;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITClientRepository extends JpaRepository<ITClient, UUID> {

    ITClient findByClientId(String clientId);

    boolean existsITClientByClientId(String clientId);
}
