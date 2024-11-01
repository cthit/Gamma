package it.chalmers.gamma.adapter.secondary.jpa.group;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostJpaRepository extends JpaRepository<PostEntity, UUID> {
  List<PostEntity> findAllByOrderByOrderAsc();
}
