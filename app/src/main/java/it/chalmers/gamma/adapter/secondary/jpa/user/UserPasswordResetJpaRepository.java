package it.chalmers.gamma.adapter.secondary.jpa.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPasswordResetJpaRepository
    extends JpaRepository<UserPasswordResetEntity, UUID> {
  Optional<UserPasswordResetEntity> findByTokenHash(String tokenHash);

  void deleteByTokenHash(String tokenHash);
}
