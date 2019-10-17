package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.Text;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextRepository extends JpaRepository<Text, UUID> {
    Text findBySv(String sv);
}
