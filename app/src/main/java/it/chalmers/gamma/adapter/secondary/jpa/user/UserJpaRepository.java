package it.chalmers.gamma.adapter.secondary.jpa.user;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

  Optional<UserEntity> findByCid(String cid);

  Optional<UserEntity> findByCidIgnoreCase(String cid);

  Optional<UserEntity> findByEmailIgnoreCase(String email);

  /**
   * Only touches the email and the version, so it can't overwrite a concurrent change to any other
   * column, such as the password.
   */
  @Transactional
  @Modifying
  @Query(
      "update UserEntity u set u.email = :email, u.version = u.version + 1, u.updatedAt = :now"
          + " where u.id = :id")
  int updateEmail(@Param("id") UUID id, @Param("email") String email, @Param("now") Instant now);

  @Transactional
  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(
      "update UserEntity u set u.password = :password, u.version = u.version + 1,"
          + " u.updatedAt = :now where u.id = :id")
  int updatePassword(
      @Param("id") UUID id, @Param("password") String password, @Param("now") Instant now);

  @Transactional
  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(
      "update UserEntity u set u.emailRecovery = :enabled, u.version = u.version + 1,"
          + " u.updatedAt = :now where u.id = :id")
  int updateEmailRecovery(
      @Param("id") UUID id, @Param("enabled") boolean enabled, @Param("now") Instant now);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select u from UserEntity u where u.id = :id")
  Optional<UserEntity> lockById(@Param("id") UUID id);

  /**
   * Locks the user and revokes every session that signed in before now. The revocation time never
   * moves backwards, so a lock that commits after a later one can't make sessions valid again.
   */
  @Transactional
  @Modifying
  @Query(
      "update UserEntity u set u.locked = true,"
          + " u.sessionsRevokedAt = case when u.sessionsRevokedAt is null"
          + " or u.sessionsRevokedAt < :now then :now else u.sessionsRevokedAt end,"
          + " u.version = u.version + 1, u.updatedAt = :now where u.id = :id")
  int lock(@Param("id") UUID id, @Param("now") Instant now);

  @Transactional
  @Modifying
  @Query(
      "update UserEntity u set u.locked = false, u.version = u.version + 1, u.updatedAt = :now"
          + " where u.id = :id")
  int unlock(@Param("id") UUID id, @Param("now") Instant now);
}
