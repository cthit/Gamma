package it.chalmers.gamma.domain.user.data;

import java.util.Optional;
import java.util.UUID;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UserId> {

    Optional<User> findByCid(Cid cid);
    Optional<User> findByEmail(Email email);

    boolean existsByCid(Cid cid);

}
