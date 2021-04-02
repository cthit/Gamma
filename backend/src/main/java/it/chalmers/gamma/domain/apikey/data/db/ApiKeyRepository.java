package it.chalmers.gamma.domain.apikey.data.db;

import it.chalmers.gamma.domain.apikey.domain.ApiKeyId;
import it.chalmers.gamma.domain.apikey.domain.ApiKeyToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, ApiKeyId> {
    boolean existsByKey(ApiKeyToken apiKey);
}
