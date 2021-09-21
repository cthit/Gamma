package it.chalmers.gamma.adapter.secondary.jpa.group;

import java.util.List;
import java.util.UUID;

import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipJpaRepository extends JpaRepository<MembershipEntity, MembershipPK> {
    List<MembershipEntity> findAllById_PostId(UUID postId);
    List<MembershipEntity> findAllById_UserId(UUID userId);
    List<MembershipEntity> findAllById_GroupId(UUID groupId);

    List<MembershipEntity> findAllById_GroupIdAndId_PostId(UUID groupId, UUID postId);
}
