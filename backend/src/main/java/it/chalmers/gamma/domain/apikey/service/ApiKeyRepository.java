package it.chalmers.gamma.domain.apikey.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, ApiKeyId> {
    boolean existsByKey(ApiKeyToken apiKey);
}
