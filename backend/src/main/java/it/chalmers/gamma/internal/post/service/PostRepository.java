package it.chalmers.gamma.internal.post.service;

import java.util.Optional;

import it.chalmers.gamma.domain.PostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, PostId> {
    Optional<PostEntity> findByPostName_Sv(String post);
    Boolean existsByPostName_Sv(String post);
}
