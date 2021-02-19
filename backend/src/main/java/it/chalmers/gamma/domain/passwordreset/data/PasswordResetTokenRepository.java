package it.chalmers.gamma.domain.passwordreset.data;

import java.util.Optional;
import java.util.UUID;

import it.chalmers.gamma.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UserId> { }
