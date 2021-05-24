package it.chalmers.gamma.internal.user.approval.service;

import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserApprovalRepository extends JpaRepository<UserApprovalEntity, UserApprovalPK> {

    List<UserApprovalEntity> findAllById_UserId(UserId userId);
    List<UserApprovalEntity> findAllById_ClientId(ClientId clientId);

}
