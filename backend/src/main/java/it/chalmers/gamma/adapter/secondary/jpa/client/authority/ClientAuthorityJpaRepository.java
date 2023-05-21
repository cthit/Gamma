package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientAuthorityJpaRepository extends JpaRepository<ClientAuthorityEntity, ClientAuthorityEntityPK> {
    List<ClientAuthorityEntity> findAllById_Client_Id(UUID clientUid);
}
