package it.chalmers.gamma.app.datadeletion;

import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientOwnerOfficial;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionReason;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequestRepository;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataDeletionRecorder {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataDeletionRecorder.class);

  private final DataDeletionRequestRepository dataDeletionRequestRepository;
  private final ClientRepository clientRepository;
  private final UserRepository userRepository;

  public DataDeletionRecorder(
      DataDeletionRequestRepository dataDeletionRequestRepository,
      ClientRepository clientRepository,
      UserRepository userRepository) {
    this.dataDeletionRequestRepository = dataDeletionRequestRepository;
    this.clientRepository = clientRepository;
    this.userRepository = userRepository;
  }

  public void recordAccountDeletion(GammaUser user) {
    this.userRepository.lockForDeletion(user.id());
    for (Client client : this.clientRepository.getClientsByUserApproved(user.id())) {
      if (isOfficial(client)) {
        create(user, client, DataDeletionReason.ACCOUNT_DELETED);
      }
    }
  }

  public void recordApprovalRevoked(GammaUser user, ClientUid clientUid) {
    this.userRepository.lockForDeletion(user.id());
    if (!this.clientRepository.isApprovedByUser(user.id(), clientUid)) {
      return;
    }

    this.clientRepository
        .get(clientUid)
        .filter(DataDeletionRecorder::isOfficial)
        .ifPresent(client -> create(user, client, DataDeletionReason.APPROVAL_REVOKED));
  }

  public void recordReApproval(UserId userId, ClientUid clientUid) {
    this.dataDeletionRequestRepository.markReApproved(userId, clientUid);
  }

  private void create(GammaUser user, Client client, DataDeletionReason reason) {
    this.dataDeletionRequestRepository.create(
        user.id(), user.cid(), client.clientUid(), client.prettyName(), reason);
    LOGGER.info(
        "Created a data deletion request for the user {} and the official client {} ({})",
        user.id().value(),
        client.clientUid().value(),
        reason);
  }

  private static boolean isOfficial(Client client) {
    return client.owner() instanceof ClientOwnerOfficial;
  }
}
