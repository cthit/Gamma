package it.chalmers.gamma.domain.text.data.db;

import it.chalmers.gamma.domain.text.TextId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextRepository extends JpaRepository<Text, TextId> {
}
