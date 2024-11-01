package it.chalmers.gamma.adapter.secondary.jpa.user;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAvatarJpaRepository extends JpaRepository<UserAvatarEntity, UUID> {}
