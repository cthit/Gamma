package it.chalmers.gamma.repository;

import it.chalmers.gamma.user.ITUser;
import it.chalmers.gamma.db.entity.PasswordResetToken;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByItUser(ITUser user);

    boolean existsByItUser(ITUser user);
}
