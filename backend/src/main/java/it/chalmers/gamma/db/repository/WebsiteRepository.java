package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Website;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsiteRepository extends JpaRepository<Website, UUID> {
    Optional<Website> findByName(String name);
}
