package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.NoAccountMembership;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoAccountMembershipRepository extends JpaRepository<NoAccountMembership, UUID> {
    List<NoAccountMembership> findAllById_FkitGroup(FKITGroup group);
}
