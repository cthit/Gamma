package it.chalmers.gamma.adapter.secondary.jpa.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

  Optional<UserEntity> findByCid(String cid);

  Optional<UserEntity> findByCidIgnoreCase(String cid);

  Optional<UserEntity> findByEmailIgnoreCase(String email);

  @Query(
      value = "SELECT CAST(user_id AS VARCHAR) FROM g_user WHERE user_id = :id FOR UPDATE",
      nativeQuery = true)
  Optional<String> lockForUpdate(@Param("id") UUID id);

  @Query("SELECT u.locked FROM UserEntity u WHERE u.id = :id")
  Optional<Boolean> findLockedById(@Param("id") UUID id);

  @Query("SELECT u.email FROM UserEntity u WHERE u.id = :id")
  Optional<String> findEmailById(@Param("id") UUID id);
}
