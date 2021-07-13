package it.chalmers.gamma.adapter.secondary.jpa.user;

import java.util.Optional;

import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UserId> {

    Optional<UserEntity> findByCid(Cid cid);
    Optional<UserEntity> findByEmail(Email email);

    boolean existsByCid(Cid cid);

}
