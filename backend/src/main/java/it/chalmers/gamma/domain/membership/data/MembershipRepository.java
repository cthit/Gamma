package it.chalmers.gamma.domain.membership.data;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, MembershipPK> {
    List<Membership> findAllById_PostId(PostId postId);
    List<Membership> findAllById_UserId(UserId userId);
    List<Membership> findAllById_GroupId(GroupId groupId);

    List<Membership> findAllById_GroupIdAndId_PostId(GroupId groupId, PostId postId);
}
