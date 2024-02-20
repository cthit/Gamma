package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientAuthorityJpaRepository
    extends JpaRepository<ClientAuthorityEntity, ClientAuthorityEntityPK> {
  List<ClientAuthorityEntity> findAllById_Client_Id(UUID clientUid);
}
