package it.chalmers.gamma.domain.approval.data;

import it.chalmers.gamma.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserApprovalRepository extends JpaRepository<UserApproval, UserApprovalPK> {

    public List<UserApproval> findAllById_UserId(UserId userId);
    public List<UserApproval> findAllById_ClientId(String clientId);

}
