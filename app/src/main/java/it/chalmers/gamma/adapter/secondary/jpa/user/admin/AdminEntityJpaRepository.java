package it.chalmers.gamma.adapter.secondary.jpa.user.admin;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminEntityJpaRepository extends JpaRepository<AdminEntity, UUID> {

  @Query(value = "SELECT CAST(pg_advisory_xact_lock(7102463) AS VARCHAR)", nativeQuery = true)
  String lockAdminChanges();

  String USABLE_ADMINS =
      "SELECT COUNT(*) FROM g_admin_user a JOIN g_user u ON u.user_id = a.user_id"
          + " WHERE u.locked IS NOT TRUE AND EXISTS ("
          + " SELECT 1 FROM user_entities e"
          + " JOIN user_credentials c ON c.user_entity_user_id = e.id"
          + " WHERE e.name = CAST(a.user_id AS VARCHAR))";

  @Query(value = USABLE_ADMINS, nativeQuery = true)
  long countUsableAdmins();

  @Query(value = USABLE_ADMINS + " AND a.user_id <> :id", nativeQuery = true)
  long countUsableAdminsOtherThan(@Param("id") UUID id);
}
