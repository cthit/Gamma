package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientAuthorityUserJpaRepository extends JpaRepository<ClientAuthorityUserEntity, ClientAuthorityUserPK> {

    List<ClientAuthorityUserEntity> findAllById_UserEntity_Id(UUID userId);
}
