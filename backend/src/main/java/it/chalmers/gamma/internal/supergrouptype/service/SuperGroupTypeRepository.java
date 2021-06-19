package it.chalmers.gamma.internal.supergrouptype.service;

import it.chalmers.gamma.domain.SuperGroupType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperGroupTypeRepository extends JpaRepository<SuperGroupTypeEntity, SuperGroupType> {
}
