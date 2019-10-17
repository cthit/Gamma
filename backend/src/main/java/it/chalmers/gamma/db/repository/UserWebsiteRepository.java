package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.ITUser;
import it.chalmers.delta.db.entity.UserWebsite;
import it.chalmers.delta.db.entity.Website;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWebsiteRepository extends JpaRepository<UserWebsite, UUID> {
    List<UserWebsite> findAllByItUser(ITUser user);

    UserWebsite findByWebsite(Website website);

    void deleteAllByItUser(ITUser user);

    void deleteAllByWebsite_Website(Website website);
}
