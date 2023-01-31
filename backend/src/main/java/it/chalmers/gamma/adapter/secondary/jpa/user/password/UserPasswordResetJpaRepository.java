package it.chalmers.gamma.adapter.secondary.jpa.user.password;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPasswordResetJpaRepository extends JpaRepository<UserPasswordResetEntity, UUID> {
    Optional<UserPasswordResetEntity> findByUserId(UUID userId);

    void deleteByToken(String token);
}
