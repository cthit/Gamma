package it.chalmers.gamma.app.throttling;

import java.time.Duration;
import java.util.List;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
public class ThrottlingService {

  private static final RedisScript<Long> UNCOUNT =
      RedisScript.of(
          "local n = redis.call('DECR', KEYS[1]) "
              + "if n <= 0 then redis.call('DEL', KEYS[1]) end "
              + "return n",
          Long.class);

  private final StringRedisTemplate redisTemplate;

  public ThrottlingService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public boolean canProceed(String key, int maxPerDay) {
    return count(key) <= maxPerDay;
  }

  /** Counts one attempt in the key's 24 hour window and returns how many it has counted. */
  public long count(String key) {
    String redisKey = "throttle:" + key;

    Long counter = redisTemplate.opsForValue().increment(redisKey, 1);

    if (counter == null) {
      throw new RedisConnectionFailureException("count not connect to redis");
    }

    if (counter == 1) {
      redisTemplate.expire(redisKey, Duration.ofHours(24));
    }

    return counter;
  }

  /** Takes back an attempt counted by {@link #count} that turned out not to count. */
  public void uncount(String key) {
    redisTemplate.execute(UNCOUNT, List.of("throttle:" + key));
  }

  public void reset(String key) {
    redisTemplate.delete("throttle:" + key);
  }
}
