package it.chalmers.gamma.domain.apikey.data;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    boolean existsByKey(String apiKey);

    Optional<ApiKey> findByName(String apiKey);
}
