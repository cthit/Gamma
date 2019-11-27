package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.GroupWebsite;
import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.domain.dto.FKITGroupDTO;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupWebsiteRepository extends JpaRepository<GroupWebsite, UUID> {
    List<GroupWebsite> findAllByGroup(FKITGroupDTO group);

    GroupWebsite findByWebsite_Website(Website website);

    void deleteAllByGroup(FKITGroupDTO group);

    void deleteAllByWebsite_Website(Website website);
}
