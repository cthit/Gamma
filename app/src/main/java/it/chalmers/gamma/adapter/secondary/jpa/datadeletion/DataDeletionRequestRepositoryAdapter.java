package it.chalmers.gamma.adapter.secondary.jpa.datadeletion;

import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionReason;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequest;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequestId;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequestRepository;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.UserId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DataDeletionRequestRepositoryAdapter implements DataDeletionRequestRepository {

  private final DataDeletionRequestJpaRepository repository;

  public DataDeletionRequestRepositoryAdapter(DataDeletionRequestJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public void create(
      UserId userId,
      Cid cid,
      ClientUid clientUid,
      PrettyName clientName,
      DataDeletionReason reason) {
    this.repository.save(
        new DataDeletionRequestEntity(
            DataDeletionRequestId.generate().value(),
            userId.value(),
            cid.value(),
            clientUid.value(),
            clientName.value(),
            reason));
  }

  @Override
  public List<DataDeletionRequest> getAllOpen() {
    return this.repository.findAllByClearedAtIsNull().stream()
        .map(DataDeletionRequestEntity::toDomain)
        .toList();
  }

  @Override
  public List<DataDeletionRequest> getAllCleared() {
    return this.repository.findAllByClearedAtIsNotNull().stream()
        .map(DataDeletionRequestEntity::toDomain)
        .toList();
  }

  @Override
  public List<DataDeletionRequest> getOpenFor(UserId userId) {
    return this.repository.findAllByUserIdAndClearedAtIsNull(userId.value()).stream()
        .map(DataDeletionRequestEntity::toDomain)
        .toList();
  }

  @Override
  public boolean hasClearedFor(UserId userId) {
    return this.repository.existsByUserIdAndClearedAtIsNotNull(userId.value());
  }

  @Override
  public void clear(DataDeletionRequestId id, UserId clearedBy)
      throws RequestNotFoundException, AlreadyClearedException {
    if (this.repository.clearIfOpen(id.value(), clearedBy.value(), Instant.now()) == 1) {
      return;
    }

    if (this.repository.existsById(id.value())) {
      throw new AlreadyClearedException();
    }

    throw new RequestNotFoundException();
  }

  @Override
  public void markReApproved(UserId userId, ClientUid clientUid) {
    this.repository.markOpenReApproved(userId.value(), clientUid.value(), Instant.now());
  }

  @Override
  public int removeClearedBefore(Instant before) {
    return this.repository.deleteAllClearedBefore(before);
  }
}
