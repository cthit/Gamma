package it.chalmers.gamma.internal.useravatar.service;

import it.chalmers.gamma.domain.UserAvatar;
import it.chalmers.gamma.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatarEntity, UserId> {
}
