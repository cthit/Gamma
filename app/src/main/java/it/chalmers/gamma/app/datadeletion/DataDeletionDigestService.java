package it.chalmers.gamma.app.datadeletion;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRecipientRepository;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequest;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequestRepository;
import it.chalmers.gamma.app.mail.domain.MailService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DataDeletionDigestService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataDeletionDigestService.class);

  private static final long CLEARED_RETENTION_DAYS = 365;

  private final DataDeletionRequestRepository dataDeletionRequestRepository;
  private final DataDeletionRecipientRepository dataDeletionRecipientRepository;
  private final MailService mailService;
  private final String baseUrl;

  public DataDeletionDigestService(
      DataDeletionRequestRepository dataDeletionRequestRepository,
      DataDeletionRecipientRepository dataDeletionRecipientRepository,
      MailService mailService,
      @Value("${application.base-url}") String baseUrl) {
    this.dataDeletionRequestRepository = dataDeletionRequestRepository;
    this.dataDeletionRecipientRepository = dataDeletionRecipientRepository;
    this.mailService = mailService;
    this.baseUrl = baseUrl;
  }

  public void sendDigest() {
    List<DataDeletionRequest> open = this.dataDeletionRequestRepository.getAllOpen();
    if (open.isEmpty()) {
      return;
    }

    List<Email> recipients = this.dataDeletionRecipientRepository.getAll();
    if (recipients.isEmpty()) {
      LOGGER.warn(
          "There are {} open data deletion requests, but no one is set to get the daily email"
              + " about them. Add a recipient at {}/data-deletion",
          open.size(),
          this.baseUrl);
      return;
    }

    Instant now = Instant.now();
    String subject = subject(open, now);
    String body = body(open, now);

    int sent = 0;
    for (Email recipient : recipients) {
      if (this.mailService.sendSystemMail(recipient.value(), subject, body)) {
        sent++;
        LOGGER.info("Sent the data deletion email to {}", recipient.value());
      } else {
        LOGGER.warn("Could not send the data deletion email to {}", recipient.value());
      }
    }

    LOGGER.info(
        "Sent the data deletion email about {} open requests to {} of {} recipients",
        open.size(),
        sent,
        recipients.size());
  }

  public void removeOldClearedRequests() {
    int removed =
        this.dataDeletionRequestRepository.removeClearedBefore(
            Instant.now().minus(CLEARED_RETENTION_DAYS, ChronoUnit.DAYS));
    if (removed > 0) {
      LOGGER.info("Removed {} data deletion requests that were cleared over a year ago", removed);
    }
  }

  private static String subject(List<DataDeletionRequest> open, Instant now) {
    long overdue = open.stream().filter(request -> request.isOverdue(now)).count();
    if (overdue > 0) {
      return "[Gamma] ACTION REQUIRED: %d overdue data deletion %s"
          .formatted(overdue, overdue == 1 ? "request" : "requests");
    }

    return "[Gamma] Action required: %d data deletion %s, the next deadline is %s"
        .formatted(open.size(), open.size() == 1 ? "request" : "requests", firstDeadline(open));
  }

  private String body(List<DataDeletionRequest> open, Instant now) {
    LocalDate today = now.atZone(DataDeletionRequest.DEADLINE_ZONE).toLocalDate();
    long overdue = open.stream().filter(request -> request.isOverdue(now)).count();

    StringBuilder body = new StringBuilder();
    body.append("There are %d open data deletion requests in Gamma.\n\n".formatted(open.size()));

    if (overdue > 0) {
      body.append(
          "%d of them are OVERDUE. Their deadline has passed, handle them as soon as possible.\n\n"
              .formatted(overdue));
    }

    body.append(
        """
        A user has deleted their account, or retracted their approval of an official client. \
        Each request must be handled within one month, as required by the GDPR. Make sure the \
        official client has deleted the data it has about the user, then mark the request as \
        cleared in Gamma.

        Deadlines:
        """);

    Map<LocalDate, Long> perDeadline =
        open.stream()
            .collect(
                Collectors.groupingBy(
                    DataDeletionRequest::deadline, TreeMap::new, Collectors.counting()));

    for (Map.Entry<LocalDate, Long> entry : perDeadline.entrySet()) {
      LocalDate deadline = entry.getKey();
      long days = ChronoUnit.DAYS.between(today, deadline);
      String status;
      if (days < 0) {
        status = "OVERDUE by %d %s".formatted(-days, -days == 1 ? "day" : "days");
      } else if (days == 0) {
        status = "due today";
      } else {
        status = "%d %s left".formatted(days, days == 1 ? "day" : "days");
      }

      body.append(
          "- %s: %d %s, %s\n"
              .formatted(
                  deadline,
                  entry.getValue(),
                  entry.getValue() == 1 ? "request" : "requests",
                  status));
    }

    body.append(
        """

        Handle the requests here:
        %s/data-deletion

        This email is sent every day until all requests are cleared.
        """
            .formatted(this.baseUrl));

    return body.toString();
  }

  private static LocalDate firstDeadline(List<DataDeletionRequest> open) {
    return open.stream().map(DataDeletionRequest::deadline).min(LocalDate::compareTo).orElseThrow();
  }
}
