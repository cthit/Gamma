package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    PasswordResetToken getTokenByItUser(ITUser user);
    boolean existsByItUser(ITUser user);
}
