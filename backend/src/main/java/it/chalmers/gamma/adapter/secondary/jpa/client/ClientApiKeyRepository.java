package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.domain.client.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientApiKeyRepository extends JpaRepository<ClientApiKeyEntity, ClientId> {
}
