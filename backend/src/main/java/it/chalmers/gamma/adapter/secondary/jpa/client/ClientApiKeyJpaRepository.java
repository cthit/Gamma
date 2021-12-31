package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.app.client.domain.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientApiKeyJpaRepository extends JpaRepository<ClientApiKeyEntity, UUID> {
    Optional<ClientApiKeyEntity> findByApiKey_Token(String apiKeyToken);

    ClientApiKeyEntity getByApiKey_Id(UUID apiKeyId);
}
