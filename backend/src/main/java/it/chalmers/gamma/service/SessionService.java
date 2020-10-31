package it.chalmers.gamma.service;

import io.lettuce.core.RedisConnectionException;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;
    private final RedisTemplate redisTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionService.class);


    public SessionService(FindByIndexNameSessionRepository<? extends Session> sessionRepository,
                          RedisTemplate redisTemplate) {
        this.sessionRepository = sessionRepository;
        this.redisTemplate = redisTemplate;
    }

    public List<Session> getSessionsByUser(String user) {
        return List.copyOf(this.sessionRepository.findByPrincipalName(user).values());
    }

    public void removeSessionFromRedis(HttpSession session) {
        RedisConnectionFactory connectionFactory = this.redisTemplate.getConnectionFactory();
        if (connectionFactory == null) {
            LOGGER.error("Connection to Redis Failed");
            throw new RedisConnectionException("Redis connection failed");
        }
        String prefix1 = "spring:session:sessions:";
        String prefix2 = "spring:session:sessions:expires:";
        RedisConnection connection = connectionFactory.getConnection();
        String sessionsKey = prefix1 + session.getId();
        String expiresKey = prefix2 + session.getId();
        connection.del(expiresKey.getBytes());
        connection.del(sessionsKey.getBytes());
    }
}
