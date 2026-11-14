package it.chalmers.gamma;

import it.chalmers.gamma.app.datadeletion.DataDeletionDigestService;
import it.chalmers.gamma.app.user.activation.domain.UserActivationRepository;
import it.chalmers.gamma.app.user.emailchange.domain.EmailChangeRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetRepository;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);

  private final UserActivationRepository userActivationRepository;
  private final PasswordResetRepository passwordResetRepository;
  private final DataDeletionDigestService dataDeletionDigestService;
  private final EmailChangeRepository emailChangeRepository;

  public ScheduledTasks(
      UserActivationRepository userActivationRepository,
      PasswordResetRepository passwordResetRepository,
      DataDeletionDigestService dataDeletionDigestService,
      EmailChangeRepository emailChangeRepository) {
    this.userActivationRepository = userActivationRepository;
    this.passwordResetRepository = passwordResetRepository;
    this.dataDeletionDigestService = dataDeletionDigestService;
    this.emailChangeRepository = emailChangeRepository;
  }

  @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 15)
  public void clearInvalidActivationCodes() {
    int i = userActivationRepository.removeInvalidActivationCodes();
    if (i > 0) {
      LOGGER.info("Removed {} expired user activation codes", i);
    }
  }

  @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 15)
  public void clearInvalidPasswordResetTokens() {
    int i = passwordResetRepository.removeInvalidPasswordResetTokens();
    if (i > 0) {
      LOGGER.info("Removed {} expired password reset tokens", i);
    }
  }

  @Scheduled(
      cron = "${application.data-deletion.digest-cron}",
      zone = "${application.data-deletion.digest-zone}")
  public void sendDataDeletionDigest() {
    dataDeletionDigestService.sendDigest();
  }

  @Scheduled(cron = "0 0 3 * * *", zone = "Europe/Stockholm")
  public void removeOldClearedDataDeletionRequests() {
    dataDeletionDigestService.removeOldClearedRequests();
  }

  @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 15)
  public void clearExpiredEmailChanges() {
    int i = emailChangeRepository.removeExpired();
    if (i > 0) {
      LOGGER.info("Removed {} expired email change requests", i);
    }
  }
}
