package it.chalmers.gamma.domain.membership.data;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import it.chalmers.gamma.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, MembershipPK> {
    List<Membership> findAllById_PostId(UUID postId);
    List<Membership> findAllById_UserId(UserId userId);
    List<Membership> findAllById_GroupId(UUID groupId);

    List<Membership> findAllById_GroupIdAndId_PostId(UUID groupId, UUID postId);
}
