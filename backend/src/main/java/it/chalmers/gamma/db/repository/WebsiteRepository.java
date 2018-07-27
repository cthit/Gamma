package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WebsiteRepository extends JpaRepository<Website, UUID> {
    Website findByName(String name);
}
