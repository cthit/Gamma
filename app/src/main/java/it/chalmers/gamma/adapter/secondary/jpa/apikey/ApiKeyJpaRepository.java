package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyJpaRepository extends JpaRepository<ApiKeyEntity, UUID> {
  Optional<ApiKeyEntity> findByToken(String token);
}
