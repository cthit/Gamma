package it.chalmers.gamma.db.repository;

import com.sun.mail.imap.AppendUID;
import it.chalmers.gamma.db.entity.ApiKey;
import it.chalmers.gamma.db.entity.AuthorityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    boolean existsByKey(String apiKey);
    ApiKey getById(UUID id);
}
