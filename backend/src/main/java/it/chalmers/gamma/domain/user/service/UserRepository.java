package it.chalmers.gamma.domain.user.service;

import java.util.Optional;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UserId> {

    Optional<User> findByCid(Cid cid);
    Optional<User> findByEmail(Email email);

    boolean existsByCid(Cid cid);

}
