package it.chalmers.gamma.adapter.secondary.jpa.signin;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRecoveryKeyJpaRepository extends JpaRepository<UserRecoveryKeyEntity, UUID> {

  boolean existsByUserIdAndKeyHash(UUID userId, String keyHash);
}
