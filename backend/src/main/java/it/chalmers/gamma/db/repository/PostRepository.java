package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    Post getByPostName(String post);
}
