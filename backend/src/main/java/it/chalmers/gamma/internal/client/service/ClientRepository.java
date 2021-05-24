package it.chalmers.gamma.internal.client.service;

import it.chalmers.gamma.domain.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, ClientId> { }
