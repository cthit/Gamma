package it.chalmers.gamma.adapter.secondary.jpa.signin;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserTotpJpaRepository extends JpaRepository<UserTotpEntity, UUID> {

  @Modifying
  @Query(
      "update UserTotpEntity t set t.lastUsedTimeStep = :timeStep"
          + " where t.userId = :userId and t.secret = :secret and t.lastUsedTimeStep < :timeStep")
  int useTimeStep(
      @Param("userId") UUID userId,
      @Param("secret") String secret,
      @Param("timeStep") long timeStep);
}
