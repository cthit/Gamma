package it.chalmers.gamma.domain.supergroup.data;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuperGroupRepository extends JpaRepository<SuperGroup, UUID> {
    Optional<SuperGroup> findByName(String name);
    boolean existsByName(String name);
}
