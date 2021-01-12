package it.chalmers.gamma.membership;

import it.chalmers.gamma.group.Group;
import it.chalmers.gamma.post.Post;
import it.chalmers.gamma.user.ITUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    List<Membership> findAllById_PostId(UUID postId);
    List<Membership> findAllById_UserId(UUID userId);
    Optional<Membership> findById_UserIdAndId_PostId(UUID userId, UUID postId);
    List<Membership> findAllById_GroupIdAndId_PostId(UUID groupId, UUID postId);
    List<Membership> findAllById_GroupId(UUID groupId);
    Optional<Membership> findById_UserIdAndId_GroupId(UUID userId, UUID groupId);
}
