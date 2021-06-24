package it.chalmers.gamma.app.client.service;

import it.chalmers.gamma.app.domain.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, ClientId> { }
