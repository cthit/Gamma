package it.chalmers.gamma.domain.userapproval.service;

import it.chalmers.gamma.domain.client.service.ClientId;
import it.chalmers.gamma.domain.user.service.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserApprovalRepository extends JpaRepository<UserApproval, UserApprovalPK> {

    List<UserApproval> findAllById_UserId(UserId userId);
    List<UserApproval> findAllById_ClientId(ClientId clientId);

}
