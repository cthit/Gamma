package it.chalmers.gamma.security;

import java.security.SecureRandom;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerBlock {
  private static final Logger LOGGER = LoggerFactory.getLogger(TimerBlock.class);
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private final long minExecutionTimeInMillis;

  private TimerBlock(long minExecutionTimeInMillis) {
    this.minExecutionTimeInMillis = minExecutionTimeInMillis;
  }

  public static TimerBlock minimum(long minExecutionTimeInMillis) {
    return new TimerBlock(minExecutionTimeInMillis);
  }

  public void execute(Runnable block) {
    SecureRandom secureRandom = new SecureRandom();
    long startTime = System.currentTimeMillis();
    RuntimeException blockException = null;
    try {
      block.run();
    } catch (RuntimeException e) {
      blockException = e;
    } finally {
      long endTime = System.currentTimeMillis();
      long actualExecutionTimeInMillis = endTime - startTime;

      long maxExecutionTime = minExecutionTimeInMillis + (long) (minExecutionTimeInMillis * 0.5);
      long randomDelay =
          minExecutionTimeInMillis
              + secureRandom.nextInt((int) (maxExecutionTime - minExecutionTimeInMillis));

      if (actualExecutionTimeInMillis < randomDelay) {
        try {
          long remainingTimeInMillis = randomDelay - actualExecutionTimeInMillis;
          scheduler.schedule(() -> {}, remainingTimeInMillis, TimeUnit.MILLISECONDS).get();
        } catch (InterruptedException | ExecutionException e) {
          LOGGER.warn("Exception occurred during delay: {}", e.getMessage());
        }
      } else {
        LOGGER.warn(
            "Block execution took longer than expected: {} milliseconds.",
            actualExecutionTimeInMillis);
      }
    }

    if (blockException != null) {
      throw blockException;
    }
  }
}
