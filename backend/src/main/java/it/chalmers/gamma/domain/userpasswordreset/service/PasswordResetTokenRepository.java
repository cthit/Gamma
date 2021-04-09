package it.chalmers.gamma.domain.userpasswordreset.service;

import it.chalmers.gamma.domain.user.service.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UserId> { }
