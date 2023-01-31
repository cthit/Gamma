package it.chalmers.gamma.adapter.secondary.jpa.group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GroupJpaRepository extends JpaRepository<GroupEntity, UUID> {
    List<GroupEntity> findAllBySuperGroupId(UUID id);
}
