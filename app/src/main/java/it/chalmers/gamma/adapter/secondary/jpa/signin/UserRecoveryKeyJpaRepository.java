package it.chalmers.gamma.adapter.secondary.jpa.signin;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRecoveryKeyJpaRepository extends JpaRepository<UserRecoveryKeyEntity, UUID> {

  @Modifying
  @Query("delete from UserRecoveryKeyEntity k where k.userId = :userId and k.keyHash = :keyHash")
  int deleteByUserIdAndKeyHash(@Param("userId") UUID userId, @Param("keyHash") String keyHash);
}
