package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.ITUser;
import it.chalmers.delta.db.entity.PasswordResetToken;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    PasswordResetToken getTokenByItUser(ITUser user);

    boolean existsByItUser(ITUser user);
}
