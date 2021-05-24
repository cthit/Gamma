package it.chalmers.gamma.internal.user.service;

import java.util.Optional;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UserId> {

    Optional<UserEntity> findByCid(Cid cid);
    Optional<UserEntity> findByEmail(Email email);

    boolean existsByCid(Cid cid);

}
