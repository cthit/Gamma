package it.chalmers.gamma.passwordreset;

import it.chalmers.gamma.user.ITUser;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
