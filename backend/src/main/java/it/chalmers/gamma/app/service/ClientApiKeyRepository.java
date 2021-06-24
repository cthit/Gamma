package it.chalmers.gamma.app.service;

import it.chalmers.gamma.app.domain.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientApiKeyRepository extends JpaRepository<ClientApiKeyEntity, ClientId> {
}
