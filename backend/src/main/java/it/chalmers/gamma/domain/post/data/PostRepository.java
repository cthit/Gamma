package it.chalmers.gamma.domain.post.data;

import java.util.Optional;
import java.util.UUID;

import it.chalmers.gamma.domain.post.PostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, PostId> {
    Optional<Post> findByPostName_Sv(String post);
    Boolean existsByPostName_Sv(String post);
}
