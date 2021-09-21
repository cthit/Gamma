package it.chalmers.gamma.adapter.secondary.jpa.user;

import java.util.Optional;
import java.util.UUID;

import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByCid(String cid);
    Optional<UserEntity> findByEmail(String email);

    boolean existsByCid(String cid);

}
