package it.chalmers.gamma.adapter.secondary.jpa.user.gdpr;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GdprTrainedJpaRepository extends JpaRepository<GdprTrainedEntity, UUID> {
}
