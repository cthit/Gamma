package it.chalmers.gamma.domain.noaccountmembership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoAccountMembershipRepository extends JpaRepository<NoAccountMembership, NoAccountMembershipPK> { }
