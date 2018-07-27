package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.GroupWebsite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupWebsiteRepository extends JpaRepository<GroupWebsite, UUID> {
    public List<GroupWebsite> findAllByGroup(FKITGroup group);
}
