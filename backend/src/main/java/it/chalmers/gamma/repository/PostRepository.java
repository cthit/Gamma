package it.chalmers.gamma.repository;

import it.chalmers.gamma.db.entity.Post;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    Optional<Post> findByPostName_Sv(String post);
    Boolean existsByPostName_Sv(String post);
}
