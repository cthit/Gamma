package it.chalmers.gamma.app.service;

import it.chalmers.gamma.app.domain.TextId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextRepository extends JpaRepository<TextEntity, TextId> {
}
