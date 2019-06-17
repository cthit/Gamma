package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    boolean existsByKey(String apiKey);
    ApiKey getById(UUID id);

    ApiKey getByName(String apiKey);
}
