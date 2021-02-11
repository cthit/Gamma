package it.chalmers.gamma.domain.membership.data;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    List<Membership> findAllById_PostId(UUID postId);
    List<Membership> findAllById_UserId(UUID userId);
    List<Membership> findAllById_GroupId(UUID groupId);

    List<Membership> findAllById_GroupIdAndId_PostId(UUID groupId, UUID postId);

    Optional<Membership> findById_UserIdAndId_PostId(UUID userId, UUID postId);
    Optional<Membership> findById_UserIdAndId_GroupId(UUID userId, UUID groupId);

    void deleteById_UserIdAndId_GroupId(UUID userId, UUID groupId);

    boolean existsById_UserIdAndId_GroupId(UUID userId, UUID groupId);
}
