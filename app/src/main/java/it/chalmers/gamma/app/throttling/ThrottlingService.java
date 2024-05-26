package it.chalmers.gamma.app.throttling;

import it.chalmers.gamma.adapter.secondary.redis.throttling.ThrottleEntryValue;
import it.chalmers.gamma.adapter.secondary.redis.throttling.ThrottlingRedisRepository;
import org.springframework.stereotype.Service;

@Service
public class ThrottlingService {

  private final ThrottlingRedisRepository throttlingRedisRepository;

  public ThrottlingService(ThrottlingRedisRepository throttlingRedisRepository) {
    this.throttlingRedisRepository = throttlingRedisRepository;
  }

  public boolean canProceed(String key) {
    if (this.throttlingRedisRepository.existsById(key)) {
      return false;
    } else {
      this.throttlingRedisRepository.save(new ThrottleEntryValue(key));
      return true;
    }
  }
}
