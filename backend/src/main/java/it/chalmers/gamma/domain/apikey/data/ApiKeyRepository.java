package it.chalmers.gamma.domain.apikey.data;

import java.util.Optional;
import java.util.UUID;

import it.chalmers.gamma.domain.apikey.ApiKeyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, ApiKeyId> {
    boolean existsByKey(String apiKey);

    Optional<ApiKey> findByName(String apiKey);
}
