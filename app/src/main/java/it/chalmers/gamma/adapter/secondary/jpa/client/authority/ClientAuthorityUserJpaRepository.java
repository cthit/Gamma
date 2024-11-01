package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientAuthorityUserJpaRepository
    extends JpaRepository<ClientAuthorityUserEntity, ClientAuthorityUserPK> {

  List<ClientAuthorityUserEntity> findAllById_UserEntity_Id(UUID userId);
}
