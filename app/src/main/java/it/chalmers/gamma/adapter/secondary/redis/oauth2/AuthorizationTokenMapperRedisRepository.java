package it.chalmers.gamma.adapter.secondary.redis.oauth2;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationTokenMapperRedisRepository
    extends CrudRepository<AuthorizationTokenMapperValue, String> {}
