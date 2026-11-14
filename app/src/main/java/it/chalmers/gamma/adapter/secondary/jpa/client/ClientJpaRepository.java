package it.chalmers.gamma.adapter.secondary.jpa.client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientJpaRepository extends JpaRepository<ClientEntity, UUID> {
  Optional<ClientEntity> findByClientId(String clientId);

  List<ClientEntity> findAllByCreatedBy(UUID userId);

  @Query(
      value = "SELECT CAST(client_uid AS VARCHAR) FROM g_client WHERE client_uid = :id FOR SHARE",
      nativeQuery = true)
  Optional<String> lockForAuthorization(@Param("id") UUID id);
}
