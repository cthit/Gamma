package it.chalmers.gamma.adapter.secondary.redis.throttling;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThrottlingRedisRepository extends CrudRepository<ThrottleEntryValue, String> {}
