package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID>{
    Membership findByPost_Id(UUID id);
    List<Membership> findAllByPost_Id(UUID id);
    List<Membership> findAllById_ItuserId(UUID id);
    Membership findById_ItuserIdAndPost(UUID userId, Post post);
    List<Membership> findAllById_FkitGroupIdAndPost(UUID groupId, Post post);
    List<Membership> findAllById_FkitGroupId(UUID groupId);
}
