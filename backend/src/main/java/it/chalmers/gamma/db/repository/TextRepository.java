package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TextRepository extends JpaRepository<Text, UUID>{
}
