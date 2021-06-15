package it.chalmers.gamma.internal.userpasswordreset.service;

import it.chalmers.gamma.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetEntity, UserId> { }
