package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthorityUserJpaRepository extends JpaRepository<AuthorityUserEntity, AuthorityUserPK> {
    List<AuthorityUserEntity> findAllById_UserEntity_Id(UUID userId);
}
