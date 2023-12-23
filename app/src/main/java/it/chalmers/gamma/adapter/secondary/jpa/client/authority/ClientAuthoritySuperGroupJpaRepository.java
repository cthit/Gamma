package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientAuthoritySuperGroupJpaRepository extends JpaRepository<ClientAuthoritySuperGroupEntity, ClientAuthoritySuperGroupPK> {
    List<ClientAuthoritySuperGroupEntity> findAllById_SuperGroupEntity_Id(UUID id);
}
