package it.chalmers.gamma.membership;

import it.chalmers.gamma.group.Group;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoAccountMembershipRepository extends JpaRepository<NoAccountMembership, UUID> {
    List<NoAccountMembership> findAllById_Group(Group group);
}
