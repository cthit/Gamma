package it.chalmers.gamma.app.datadeletion;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRecipientRepository;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequest;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequestId;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequestRepository;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DataDeletionFacade extends Facade {

  private static final int MAX_RECIPIENT_LENGTH = 100;

  private final DataDeletionRequestRepository dataDeletionRequestRepository;
  private final DataDeletionRecipientRepository dataDeletionRecipientRepository;
  private final UserRepository userRepository;

  public DataDeletionFacade(
      AccessGuard accessGuard,
      DataDeletionRequestRepository dataDeletionRequestRepository,
      DataDeletionRecipientRepository dataDeletionRecipientRepository,
      UserRepository userRepository) {
    super(accessGuard);
    this.dataDeletionRequestRepository = dataDeletionRequestRepository;
    this.dataDeletionRecipientRepository = dataDeletionRecipientRepository;
    this.userRepository = userRepository;
  }

  public List<OpenRequestDTO> getOpenRequests() {
    this.accessGuard.require(isAdmin());

    Instant now = Instant.now();
    return this.dataDeletionRequestRepository.getAllOpen().stream()
        .sorted(Comparator.comparing(DataDeletionRequest::createdAt))
        .map(request -> new OpenRequestDTO(request, now))
        .toList();
  }

  public List<ClearedRequestDTO> getClearedRequests() {
    this.accessGuard.require(isAdmin());

    return this.dataDeletionRequestRepository.getAllCleared().stream()
        .sorted(Comparator.comparing(DataDeletionRequest::clearedAt).reversed())
        .map(request ->
            new ClearedRequestDTO(
                request,
                Optional.ofNullable(request.clearedBy())
                    .flatMap(this.userRepository::get)
                    .map(ClearedRequestDTO::name)
                    .orElse(null)))
        .toList();
  }

  public void clear(UUID requestId)
      throws DataDeletionRequestRepository.RequestNotFoundException,
          DataDeletionRequestRepository.AlreadyClearedException {
    this.accessGuard.require(isAdmin());

    if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication admin) {
      UserId adminId = admin.gammaUser().id();
      this.dataDeletionRequestRepository.clear(new DataDeletionRequestId(requestId), adminId);
      // The transactional request row is the audit record; do not duplicate user/request
      // associations in logs with a separate, potentially longer retention period.
    }
  }

  public List<String> getRecipients() {
    this.accessGuard.require(isAdmin());

    return this.dataDeletionRecipientRepository.getAll().stream().map(Email::value).toList();
  }

  public void addRecipient(String email)
      throws DataDeletionRecipientRepository.RecipientAlreadyExistsException {
    this.accessGuard.require(isAdmin());

    String trimmed = email.trim();
    if (trimmed.length() > MAX_RECIPIENT_LENGTH) {
      throw new IllegalArgumentException("Email is too long");
    }

    this.dataDeletionRecipientRepository.add(new Email(trimmed));
  }

  public void removeRecipient(String email)
      throws DataDeletionRecipientRepository.RecipientNotFoundException {
    this.accessGuard.require(isAdmin());

    this.dataDeletionRecipientRepository.remove(new Email(email));
  }

  public Optional<ActionRequiredDTO> getActionRequired() {
    this.accessGuard.require(isAdmin());

    List<DataDeletionRequest> open = this.dataDeletionRequestRepository.getAllOpen();
    Instant now = Instant.now();
    long overdue = open.stream().filter(request -> request.isOverdue(now)).count();
    boolean noRecipients = this.dataDeletionRecipientRepository.getAll().isEmpty();

    if (overdue == 0 && !noRecipients) {
      return Optional.empty();
    }

    return Optional.of(new ActionRequiredDTO(open.size(), overdue, noRecipients));
  }

  public record OpenRequestDTO(
      UUID id,
      UUID userId,
      String cid,
      UUID clientUid,
      String clientName,
      String reason,
      LocalDate createdOn,
      LocalDate deadline,
      boolean overdue,
      long daysLeft,
      LocalDate reApprovedOn) {
    public OpenRequestDTO(DataDeletionRequest request, Instant now) {
      this(
          request.id().value(),
          request.userId().value(),
          request.cid().value(),
          request.clientUid().value(),
          request.clientName().value(),
          request.reason().name(),
          date(request.createdAt()),
          request.deadline(),
          request.isOverdue(now),
          ChronoUnit.DAYS.between(date(now), request.deadline()),
          date(request.reApprovedAt()));
    }

    public boolean reApproved() {
      return this.reApprovedOn != null;
    }
  }

  public record ClearedRequestDTO(
      UUID id,
      UUID userId,
      UUID clientUid,
      String reason,
      LocalDate createdOn,
      UUID clearedBy,
      String clearedByName,
      LocalDate clearedOn) {
    public ClearedRequestDTO(DataDeletionRequest request, String clearedByName) {
      this(
          request.id().value(),
          request.userId().value(),
          request.clientUid().value(),
          request.reason().name(),
          date(request.createdAt()),
          request.clearedBy() == null ? null : request.clearedBy().value(),
          clearedByName,
          date(request.clearedAt()));
    }

    private static String name(GammaUser user) {
      return "%s '%s' %s"
          .formatted(user.firstName().value(), user.nick().value(), user.lastName().value());
    }
  }

  private static LocalDate date(Instant instant) {
    return instant == null ? null : instant.atZone(DataDeletionRequest.DEADLINE_ZONE).toLocalDate();
  }

  public record ActionRequiredDTO(int open, long overdue, boolean noRecipients) {}
}
