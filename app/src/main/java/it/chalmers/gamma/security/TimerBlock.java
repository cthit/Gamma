package it.chalmers.gamma.security;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerBlock {

  private static final Logger LOGGER = LoggerFactory.getLogger(TimerBlock.class);
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private final long minExecutionTimeInSeconds;

  private TimerBlock(long minExecutionTimeInSeconds) {
    this.minExecutionTimeInSeconds = minExecutionTimeInSeconds;
  }

  public static TimerBlock minimum(long minExecutionTimeInSeconds) {
    return new TimerBlock(minExecutionTimeInSeconds);
  }

  public void execute(Runnable block) {
    long startTime = System.currentTimeMillis();
    RuntimeException blockException = null;
    try {
      block.run();
    } catch (RuntimeException e) {
      blockException = e;
    } finally {
      long endTime = System.currentTimeMillis();
      long actualExecutionTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);

      if (actualExecutionTimeInSeconds < minExecutionTimeInSeconds) {
        try {
          long remainingTimeInSeconds = minExecutionTimeInSeconds - actualExecutionTimeInSeconds;
          scheduler.schedule(() -> {}, remainingTimeInSeconds, TimeUnit.SECONDS).get();
        } catch (InterruptedException | ExecutionException e) {
          LOGGER.warn("Exception occurred during delay: {}", e.getMessage());
        }
      } else {
        LOGGER.warn(
            "Block execution took longer than expected: {} seconds.", actualExecutionTimeInSeconds);
      }
    }

    if (blockException != null) {
      throw blockException;
    }
  }
}
