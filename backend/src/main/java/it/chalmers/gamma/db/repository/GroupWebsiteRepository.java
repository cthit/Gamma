package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.FKITGroup;
import it.chalmers.delta.db.entity.GroupWebsite;
import it.chalmers.delta.db.entity.Website;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupWebsiteRepository extends JpaRepository<GroupWebsite, UUID> {
    List<GroupWebsite> findAllByGroup(FKITGroup group);

    GroupWebsite findByWebsite_Website(Website website);

    void deleteAllByGroup(FKITGroup group);

    void deleteAllByWebsite_Website(Website website);
}
