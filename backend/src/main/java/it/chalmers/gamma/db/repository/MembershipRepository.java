package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    List<Membership> findAllById_Post(Post post);
    List<Membership> findAllById_ItUser(ITUser itUser);
    Optional<Membership> findById_ItUserAndId_Post(ITUser user, Post post);
    List<Membership> findAllById_FkitGroupAndId_Post(FKITGroup group, Post post);
    List<Membership> findAllById_FkitGroup(FKITGroup group);
    Optional<Membership> findById_ItUserAndId_FkitGroup(ITUser user, FKITGroup group);
}
