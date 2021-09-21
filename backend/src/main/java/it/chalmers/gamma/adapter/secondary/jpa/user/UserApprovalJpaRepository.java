package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserApprovalJpaRepository extends JpaRepository<UserApprovalEntity, UserApprovalEntityPK> {

}
