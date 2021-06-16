package it.chalmers.gamma.internal.groupimages.service;

import it.chalmers.gamma.domain.GroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupImagesRepository extends JpaRepository<GroupImagesEntity, GroupId> { }
