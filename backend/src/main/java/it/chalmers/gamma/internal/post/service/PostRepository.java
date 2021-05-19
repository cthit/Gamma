package it.chalmers.gamma.internal.post.service;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, PostId> {
    Optional<Post> findByPostName_Sv(String post);
    Boolean existsByPostName_Sv(String post);
}