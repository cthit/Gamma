package it.chalmers.gamma.app.throttling;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ThrottlingService {

  private final StringRedisTemplate redisTemplate;

  public ThrottlingService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public boolean canProceed(String key, int maxPerDay) {
    String redisKey = "throttle:" + key;

    Long counter = redisTemplate.opsForValue().increment(redisKey, 1);

    if (counter == null) {
      throw new RedisConnectionFailureException("count not connect to redis");
    }

    if (counter == 1) {
      redisTemplate.expire(redisKey, 24, TimeUnit.HOURS);
    }

    return counter <= maxPerDay;
  }
}
