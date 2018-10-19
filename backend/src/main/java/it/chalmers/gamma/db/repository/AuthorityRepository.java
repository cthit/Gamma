package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Authority;
import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Authority findById_FkitGroupAndAndId_Post(FKITGroup group, Post post);
}
