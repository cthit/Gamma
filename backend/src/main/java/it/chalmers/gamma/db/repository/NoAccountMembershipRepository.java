package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.NoAccountMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoAccountMembershipRepository extends JpaRepository<NoAccountMembership, UUID> {

}
