package it.chalmers.gamma.adapter.secondary.jpa.user.gdpr;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GdprTrainedJpaRepository extends JpaRepository<GdprTrainedEntity, UUID> {}
