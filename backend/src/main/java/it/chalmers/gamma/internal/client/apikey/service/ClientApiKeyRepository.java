package it.chalmers.gamma.internal.client.apikey.service;

import it.chalmers.gamma.internal.client.service.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientApiKeyRepository extends JpaRepository<ClientApiKey, ClientId> {
}
