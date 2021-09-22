package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthorityPostJpaRepository extends JpaRepository<AuthorityPostEntity, AuthorityPostPK> {
    List<AuthorityPostEntity> findAllById_SuperGroupEntity_Id_AndId_PostEntity_Id(UUID superGroupId, UUID postId);
}
