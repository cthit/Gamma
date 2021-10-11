package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuperGroupJpaRepository extends JpaRepository<SuperGroupEntity, UUID> {

    List<SuperGroupEntity> findAllBySuperGroupType(String type);

}
