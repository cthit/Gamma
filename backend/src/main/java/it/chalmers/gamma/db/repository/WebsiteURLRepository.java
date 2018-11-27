package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteURL;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsiteURLRepository extends JpaRepository<WebsiteURL, UUID> {
    void deleteAllByWebsite(Website website);
}
