package it.chalmers.gamma.app.throttling;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ThrottlingManagementFacade extends Facade {

  private final StringRedisTemplate redisTemplate;

  public ThrottlingManagementFacade(AccessGuard accessGuard, StringRedisTemplate redisTemplate) {
    super(accessGuard);
    this.redisTemplate = redisTemplate;
  }

  public record KeyValue(String key, String value) {}

  public List<KeyValue> getAll() {
    super.accessGuard.require(isAdmin());

    Set<String> keys = redisTemplate.keys("throttle:*");
    List<KeyValue> result = new ArrayList<>();

    if (keys != null) {
      for (String key : keys) {
        String value = redisTemplate.opsForValue().get(key);
        String trimmedKey = key.substring("throttle:".length());
        result.add(new KeyValue(trimmedKey, value));
      }
    }

    return result;
  }

  public void deleteKey(String key) {
    super.accessGuard.require(isAdmin());

    String prefixedKey = "throttle:" + key;
    redisTemplate.delete(prefixedKey);
  }
}
