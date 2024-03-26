package it.chalmers.gamma.adapter.secondary.jpa.user;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserApprovalJpaRepository
    extends JpaRepository<UserApprovalEntity, UserApprovalEntityPK> {
  List<UserApprovalEntity> findAllById_User_Id(UUID id);

  List<UserApprovalEntity> findAllById_Client_ClientUid(UUID id);

  List<UserApprovalEntity> findAllById_Client_ClientId(String id);

  boolean existsById_Client_ClientUidAndId_User_Id(UUID clientUid, UUID userId);

  void deleteById_Client_ClientUidAndId_User_Id(UUID clientUid, UUID userId);
}
