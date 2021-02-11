package it.chalmers.gamma.domain.noaccountmembership;

import it.chalmers.gamma.domain.group.data.Group;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoAccountMembershipRepository extends JpaRepository<NoAccountMembership, UUID> {
    List<NoAccountMembership> findAllById_Group(Group group);
}
