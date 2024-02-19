package it.chalmers.gamma.adapter.secondary.jpa.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientJpaRepository extends JpaRepository<ClientEntity, UUID> {
    Optional<ClientEntity> findByClientId(String clientId);
    List<ClientEntity> findAllByCreatedBy(UUID userId);
}
