package it.chalmers.gamma.adapter.secondary.jpa.datadeletion;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DataDeletionRequestJpaRepository
    extends JpaRepository<DataDeletionRequestEntity, UUID> {

  List<DataDeletionRequestEntity> findAllByClearedAtIsNull();

  List<DataDeletionRequestEntity> findAllByClearedAtIsNotNull();

  List<DataDeletionRequestEntity> findAllByUserIdAndClearedAtIsNull(UUID userId);

  boolean existsByUserIdAndClearedAtIsNotNull(UUID userId);

  @Modifying
  @Query(
      "UPDATE DataDeletionRequestEntity r SET r.cid = NULL, r.clientName = NULL,"
          + " r.clearedBy = :clearedBy, r.clearedAt = :now"
          + " WHERE r.id = :id AND r.clearedAt IS NULL")
  int clearIfOpen(
      @Param("id") UUID id, @Param("clearedBy") UUID clearedBy, @Param("now") Instant now);

  @Modifying
  @Query(
      "UPDATE DataDeletionRequestEntity r SET r.reApprovedAt = :now"
          + " WHERE r.userId = :userId AND r.clientUid = :clientUid"
          + " AND r.clearedAt IS NULL AND r.reApprovedAt IS NULL")
  int markOpenReApproved(
      @Param("userId") UUID userId, @Param("clientUid") UUID clientUid, @Param("now") Instant now);

  @Modifying
  @Query("DELETE FROM DataDeletionRequestEntity r WHERE r.clearedAt < :before")
  int deleteAllClearedBefore(@Param("before") Instant before);
}
