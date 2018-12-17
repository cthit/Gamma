package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.ITClient;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITClientDetailsRepository extends JpaRepository<ITClient, UUID> {

    ITClient findByClientId(String clientId);

}
