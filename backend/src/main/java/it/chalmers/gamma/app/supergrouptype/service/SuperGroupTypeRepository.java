package it.chalmers.gamma.app.supergrouptype.service;

import it.chalmers.gamma.app.domain.SuperGroupType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperGroupTypeRepository extends JpaRepository<SuperGroupTypeEntity, SuperGroupType> {
}
