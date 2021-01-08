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
    List<Membership> findAllById_Post(Post post);
    List<Membership> findAllById_PostId(UUID id);
    List<Membership> findAllById_ItUser(ITUser itUser);
    Optional<Membership> findById_ItUserAndId_Post(ITUser user, Post post);
    List<Membership> findAllById_GroupAndId_Post(Group group, Post post);
    List<Membership> findAllById_Group(Group group);
    Optional<Membership> findById_ItUserAndId_Group(ITUser user, Group group);
}
