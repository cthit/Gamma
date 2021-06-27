package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.app.domain.ApiKeyId;
import it.chalmers.gamma.app.domain.ApiKeyToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyJpaRepository extends JpaRepository<ApiKeyEntity, ApiKeyId> {
    Optional<ApiKeyEntity> findByToken(ApiKeyToken apiKeyToken);
}