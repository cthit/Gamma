package it.chalmers.gamma.domain;

import it.chalmers.gamma.db.entity.Text;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextRepository extends JpaRepository<Text, UUID> {
    Text findBySv(String sv);
}
