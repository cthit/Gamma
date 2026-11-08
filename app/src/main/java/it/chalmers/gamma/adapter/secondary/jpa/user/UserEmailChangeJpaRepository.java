package it.chalmers.gamma.adapter.secondary.jpa.user;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEmailChangeJpaRepository extends JpaRepository<UserEmailChangeEntity, UUID> {

  @Query(
      "select e from UserEmailChangeEntity e"
          + " where e.tokenHash = :tokenHash and e.userId = :userId and e.createdAt > :cutoff")
  Optional<UserEmailChangeEntity> findValid(
      @Param("tokenHash") String tokenHash,
      @Param("userId") UUID userId,
      @Param("cutoff") Instant cutoff);

  @Modifying
  @Query(
      "delete from UserEmailChangeEntity e"
          + " where e.tokenHash = :tokenHash and e.userId = :userId and e.createdAt > :cutoff")
  int deleteValid(
      @Param("tokenHash") String tokenHash,
      @Param("userId") UUID userId,
      @Param("cutoff") Instant cutoff);

  @Modifying
  @Query("delete from UserEmailChangeEntity e where e.createdAt <= :cutoff")
  int deleteExpired(@Param("cutoff") Instant cutoff);
}
