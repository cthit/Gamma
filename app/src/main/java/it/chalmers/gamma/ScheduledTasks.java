package it.chalmers.gamma;

import it.chalmers.gamma.app.user.activation.domain.UserActivationRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);

    private final UserActivationRepository userActivationRepository;
    private final PasswordResetRepository passwordResetRepository;

    public ScheduledTasks(UserActivationRepository userActivationRepository, PasswordResetRepository passwordResetRepository) {
        this.userActivationRepository = userActivationRepository;
        this.passwordResetRepository = passwordResetRepository;
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
}
