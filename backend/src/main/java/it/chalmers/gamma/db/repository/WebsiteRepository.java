package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Website;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsiteRepository extends JpaRepository<Website, UUID> {
    Website findByName(String name);
}
