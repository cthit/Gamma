package it.chalmers.gamma.domain.membership.service;

import java.util.List;

import it.chalmers.gamma.domain.group.service.GroupId;
import it.chalmers.gamma.domain.post.service.PostId;
import it.chalmers.gamma.domain.user.service.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, MembershipPK> {
    List<Membership> findAllById_PostId(PostId postId);
    List<Membership> findAllById_UserId(UserId userId);
    List<Membership> findAllById_GroupId(GroupId groupId);

    List<Membership> findAllById_GroupIdAndId_PostId(GroupId groupId, PostId postId);
}
