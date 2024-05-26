package it.chalmers.gamma.adapter.secondary.redis.throttling;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "throttle-entry", timeToLive = 60)
public class ThrottleEntryValue {

  @Id String key;

  public ThrottleEntryValue() {}

  public ThrottleEntryValue(String key) {
    this.key = key;
  }
}
