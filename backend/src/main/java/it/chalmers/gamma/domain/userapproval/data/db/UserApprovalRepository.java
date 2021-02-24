package it.chalmers.gamma.domain.userapproval.data.db;

import it.chalmers.gamma.domain.client.domain.ClientId;
import it.chalmers.gamma.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserApprovalRepository extends JpaRepository<UserApproval, UserApprovalPK> {

    public List<UserApproval> findAllById_UserId(UserId userId);
    public List<UserApproval> findAllById_ClientId(ClientId clientId);

}
