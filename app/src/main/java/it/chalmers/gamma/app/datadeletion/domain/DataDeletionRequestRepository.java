package it.chalmers.gamma.app.datadeletion.domain;

import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.UserId;
import java.time.Instant;
import java.util.List;

public interface DataDeletionRequestRepository {

  void create(
      UserId userId,
      Cid cid,
      ClientUid clientUid,
      PrettyName clientName,
      DataDeletionReason reason);

  List<DataDeletionRequest> getAllOpen();

  List<DataDeletionRequest> getAllCleared();

  void clear(DataDeletionRequestId id, UserId clearedBy)
      throws RequestNotFoundException, AlreadyClearedException;

  void markReApproved(UserId userId, ClientUid clientUid);

  int removeClearedBefore(Instant before);

  class RequestNotFoundException extends Exception {}

  class AlreadyClearedException extends Exception {}
}
