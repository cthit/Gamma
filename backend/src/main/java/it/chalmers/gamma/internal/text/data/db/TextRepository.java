package it.chalmers.gamma.internal.text.data.db;

import it.chalmers.gamma.internal.text.TextId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextRepository extends JpaRepository<Text, TextId> {
}
