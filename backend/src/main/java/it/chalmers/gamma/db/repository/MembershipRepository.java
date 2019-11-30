package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    List<Membership> findAllById_Post(Post post);

    List<Membership> findAllById_ItUser(ITUserDTO itUser);

    Membership findById_ItUserAndId_Post(ITUserDTO user, Post post);

    List<Membership> findAllById_FkitGroupAndId_Post(FKITGroupDTO group, Post post);

    List<Membership> findAllById_FkitGroup(FKITGroupDTO group);

    Membership findById_ItUserAndId_FkitGroup(ITUserDTO user, FKITGroupDTO group);
}
