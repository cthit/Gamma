package it.chalmers.gamma.adapter.secondary.jpa.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MembershipJpaRepository extends JpaRepository<MembershipEntity, MembershipPK> {
    List<MembershipEntity> findAllById_Post_Id(UUID postId);

    List<MembershipEntity> findAllById_User_Id(UUID userId);

    List<MembershipEntity> findAllById_Group_Id(UUID groupId);

    List<MembershipEntity> findAllById_GroupIdAndId_PostId(UUID groupId, UUID postId);
}
