package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.app.domain.client.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientApiKeyJpaRepository extends JpaRepository<ClientApiKeyEntity, ClientId> {
    Optional<ClientEntity> findByApiKey_Token(String apiKeyToken);
}
