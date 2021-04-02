package it.chalmers.gamma.domain.supergroup.data;

import java.util.Optional;

import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuperGroupRepository extends JpaRepository<SuperGroup, SuperGroupId> {
    Optional<SuperGroup> findByName(String name);
    boolean existsByName(String name);
}
