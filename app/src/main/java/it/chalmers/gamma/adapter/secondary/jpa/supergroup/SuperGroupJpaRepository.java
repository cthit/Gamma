package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuperGroupJpaRepository extends JpaRepository<SuperGroupEntity, UUID> {
  List<SuperGroupEntity> findAllBySuperGroupType_Name(String type);
}
