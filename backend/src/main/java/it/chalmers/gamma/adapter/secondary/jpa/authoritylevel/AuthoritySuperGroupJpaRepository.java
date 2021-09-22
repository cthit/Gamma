package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthoritySuperGroupJpaRepository extends JpaRepository<AuthoritySuperGroupEntity, AuthoritySuperGroupPK> {
    List<AuthoritySuperGroupEntity> findAllById_SuperGroupEntity_Id(UUID id);
}
