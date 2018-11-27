package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Post;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    Post getByPostName_Sv(String post);
}
