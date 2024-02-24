package it.chalmers.gamma.adapter.secondary.jpa.group;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipJpaRepository extends JpaRepository<MembershipEntity, MembershipPK> {
  List<MembershipEntity> findAllById_Post_Id(UUID postId);

  List<MembershipEntity> findAllById_User_Id(UUID userId);
}
