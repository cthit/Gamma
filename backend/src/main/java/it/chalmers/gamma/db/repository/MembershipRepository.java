package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.FKITGroup;
import it.chalmers.delta.db.entity.ITUser;
import it.chalmers.delta.db.entity.Membership;
import it.chalmers.delta.db.entity.Post;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    List<Membership> findAllById_Post(Post post);

    List<Membership> findAllById_ItUser(ITUser itUser);

    Membership findById_ItUserAndId_Post(ITUser user, Post post);

    List<Membership> findAllById_FkitGroupAndId_Post(FKITGroup group, Post post);

    List<Membership> findAllById_FkitGroup(FKITGroup group);

    Membership findById_ItUserAndId_FkitGroup(ITUser user, FKITGroup group);
}
