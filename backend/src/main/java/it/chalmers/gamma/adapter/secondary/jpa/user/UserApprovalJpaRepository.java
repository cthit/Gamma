package it.chalmers.gamma.adapter.secondary.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserApprovalJpaRepository extends JpaRepository<UserApprovalEntity, UserApprovalEntityPK> {

}
