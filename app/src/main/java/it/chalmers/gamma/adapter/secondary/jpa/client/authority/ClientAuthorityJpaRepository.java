package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientAuthorityJpaRepository
    extends JpaRepository<ClientAuthorityEntity, ClientAuthorityEntityPK> {
  List<ClientAuthorityEntity> findAllById_Client_Id(UUID clientUid);

  @Query(
      value =
          """
          SELECT DISTINCT a.* FROM g_client_authority a
              LEFT JOIN g_client_authority_user uau ON a.client_uid = uau.client_uid AND a.authority_name = uau.authority_name
              WHERE uau.user_id = :userId AND uau.client_uid = :clientUid

          UNION

          SELECT DISTINCT au.* FROM g_client_authority au
              INNER JOIN g_client_authority_super_group sga ON au.client_uid = sga.client_uid AND au.authority_name = sga.authority_name
              INNER JOIN g_super_group sg ON sg.super_group_id = sga.super_group_id
              INNER JOIN g_group g ON g.super_group_id = sg.super_group_id
              INNER JOIN g_membership m ON m.group_id = g.group_id
          WHERE m.user_id = :userId AND au.client_uid = :clientUid
                  """,
      nativeQuery = true)
  List<ClientAuthorityEntity> findAllClientAuthoritiesByUser(
      @Param("clientUid") UUID clientUid, @Param("userId") UUID userId);
}
