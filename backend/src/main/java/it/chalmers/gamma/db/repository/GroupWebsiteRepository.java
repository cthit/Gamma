package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupWebsiteRepository extends JpaRepository<GroupWebsite, UUID> {
    List<GroupWebsite> findAllByGroup(FKITGroup group);
    GroupWebsite findByWebsite_Website(Website website);
    void deleteAllByGroup(FKITGroup group);
    void deleteAllByWebsite_Website(Website website);
}
