package it.chalmers.gamma.domain.client.data;

import it.chalmers.gamma.domain.client.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, ClientId> { }
