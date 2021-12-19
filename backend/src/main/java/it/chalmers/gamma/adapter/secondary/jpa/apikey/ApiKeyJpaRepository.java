package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApiKeyJpaRepository extends JpaRepository<ApiKeyEntity, UUID> {
    Optional<ApiKeyEntity> findByToken(String token);
}