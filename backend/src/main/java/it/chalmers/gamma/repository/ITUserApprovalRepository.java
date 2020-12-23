package it.chalmers.gamma.repository;

import it.chalmers.gamma.db.entity.ITUserApproval;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ITUserApprovalRepository extends JpaRepository<ITUserApproval, UUID> {

    ITUserApproval findById_ItUserCidContainingAndId_ItClient_ClientIdContaining(String cid, String clientId);
    List<ITUserApproval> findAllById_ItClient_ClientIdContaining(String clientId);
    List<ITUserApproval> findAllById_ItUser_CidContaining(String cid);

}
