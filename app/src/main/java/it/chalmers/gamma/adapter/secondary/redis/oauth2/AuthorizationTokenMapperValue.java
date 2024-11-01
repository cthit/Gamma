package it.chalmers.gamma.adapter.secondary.redis.oauth2;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "rawToken", timeToLive = 3600)
public class AuthorizationTokenMapperValue {

  @Id String tokenKey;

  String authorizationKey;

  public AuthorizationTokenMapperValue() {}

  public AuthorizationTokenMapperValue(String authorizationKey, String type, String token) {
    this.tokenKey = type + ":" + token;
    this.authorizationKey = authorizationKey;
  }
}
