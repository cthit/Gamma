package it.chalmers.gamma.domain.client.data.db;

import it.chalmers.gamma.domain.client.domain.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, ClientId> { }
