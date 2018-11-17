package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID>{
    Membership findByPost(Post post);
    List<Membership> findAllByPost(Post post);
    List<Membership> findAllById_ItUser(ITUser itUser);
    Membership findById_ItUserAndPost(ITUser user, Post post);
    List<Membership> findAllById_FkitGroupAndPost(FKITGroup group, Post post);
    List<Membership> findAllById_FkitGroup(FKITGroup group);
    Membership findById_ItUserAndId_FkitGroup(ITUser user, FKITGroup group);
}
