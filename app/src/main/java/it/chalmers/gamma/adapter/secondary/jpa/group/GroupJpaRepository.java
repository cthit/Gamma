package it.chalmers.gamma.adapter.secondary.jpa.group;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupJpaRepository extends JpaRepository<GroupEntity, UUID> {
  List<GroupEntity> findAllBySuperGroupId(UUID id);

  Optional<GroupEntity> findByName(String name);
}
