package it.chalmers.gamma.approval.data;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserApprovalRepository extends JpaRepository<UserApproval, UUID> {

    UserApproval findById_ItUserCidContainingAndId_ItClient_ClientIdContaining(String cid, String clientId);
    List<UserApproval> findAllById_ItClient_ClientIdContaining(String clientId);
    List<UserApproval> findAllById_ItUser_CidContaining(String cid);

}
