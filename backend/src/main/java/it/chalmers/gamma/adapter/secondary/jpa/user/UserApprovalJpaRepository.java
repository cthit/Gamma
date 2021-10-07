package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserApprovalJpaRepository extends JpaRepository<UserApprovalEntity, UserApprovalEntityPK> {
    List<ClientEntity> findAllById_User_Id(UUID id);
}
